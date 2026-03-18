package com.saas.legit.module.identity.service;

import com.saas.legit.core.service.CloudinaryService;
import com.saas.legit.module.catalog.model.LawFirm;
import com.saas.legit.module.catalog.repository.LawFirmRepository;
import com.saas.legit.module.identity.dto.UpdateClientProfileRequest;
import com.saas.legit.module.identity.dto.UpdateLawyerProfileRequest;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.model.LawyerProfile;
import com.saas.legit.module.marketplace.repository.LawyerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final ClientProfileRepository clientProfileRepository;
    private final LawyerProfileRepository lawyerProfileRepository;
    private final LawFirmRepository lawFirmRepository;

    @Transactional
    public void updateClientProfile(Long userId, UpdateClientProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        ClientProfile profile = clientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));

        // Update User info
        updateUserBasicInfo(user, request.firstName(), request.lastNameFather(), request.lastNameMother(), request.phoneNumber());

        // Update Client specific info
        profile.setCompanyName(request.companyName());
        profile.setBillingAddress(request.billingAddress());

        userRepository.save(user);
        clientProfileRepository.save(profile);
    }

    @Transactional
    public void updateLawyerProfile(Long userId, UpdateLawyerProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Lawyer profile not found"));

        updateUserBasicInfo(user, request.firstName(), request.lastNameFather(), request.lastNameMother(), request.phoneNumber());

        profile.setBioLawyer(request.bio());
        profile.setCity(request.city());
        profile.setCountry(request.country());
        profile.setHourlyRate(request.hourlyRate());
        profile.setCurrency(request.currency());
        profile.setBarRegistrationNumber(request.barRegistrationNumber());
        profile.setBarAssociation(request.barAssociation());

        userRepository.save(user);
        lawyerProfileRepository.save(profile);
    }

    private void updateUserBasicInfo(User user, String firstName, String lastNameFather, String lastNameMother, String phone) {
        user.setFirstName(firstName);
        user.setLastNameFather(lastNameFather);
        user.setLastNameMother(lastNameMother);
        user.setPhoneNumber(phone);
    }

    @Transactional
    public String updateAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();
        String url = cloudinaryService.uploadFile(file, "avatars");
        user.setAvatarURL(url);
        userRepository.save(user);
        return url;
    }

    @Transactional
    public String updateClientCompanyLogo(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId).orElseThrow();
        ClientProfile profile = clientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Client profile not found"));
        
        String url = cloudinaryService.uploadFile(file, "client_logos");
        profile.setCompanyURL(url);
        clientProfileRepository.save(profile);
        return url;
    }

    @Transactional
    public String updateLawFirmLogo(Long userId, MultipartFile file) throws IOException {
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil de abogado no encontrado"));
        
        LawFirm lawFirm = profile.getLawFirm();
        if (lawFirm == null) {
            lawFirm = new LawFirm();
            lawFirm.setNameLawFirm("Mi Despacho Legal");
            lawFirm = lawFirmRepository.save(lawFirm);
            profile.setLawFirm(lawFirm);
            lawyerProfileRepository.save(profile);
        }

        String url = cloudinaryService.uploadFile(file, "law_firm_logos");
        lawFirm.setLogoUrl(url);
        lawFirmRepository.save(lawFirm);
        return url;
    }

    @Transactional
    public String updateLawFirmCover(Long userId, MultipartFile file) throws IOException {
        LawyerProfile profile = lawyerProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Perfil de abogado no encontrado"));
        
        LawFirm lawFirm = profile.getLawFirm();
        if (lawFirm == null) {
            lawFirm = new LawFirm();
            lawFirm.setNameLawFirm("Mi Despacho Legal");
            lawFirm = lawFirmRepository.save(lawFirm);
            profile.setLawFirm(lawFirm);
            lawyerProfileRepository.save(profile);
        }

        String url = cloudinaryService.uploadFile(file, "law_firm_covers");
        lawFirm.setCoverPhotoUrl(url);
        lawFirmRepository.save(lawFirm);
        return url;
    }

    @Transactional(readOnly = true)
    public com.saas.legit.module.identity.dto.PublicProfileResponse getPublicProfile(String slug) {
        LawyerProfile profile = lawyerProfileRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Perfil público no encontrado: " + slug));

        User user = profile.getUser();
        LawFirm lawFirm = profile.getLawFirm();

        List<com.saas.legit.module.identity.dto.PublicProfileResponse.SpecialtyDTO> specialtyDTOs = profile.getSpecialties().stream()
                .map(s -> new com.saas.legit.module.identity.dto.PublicProfileResponse.SpecialtyDTO(s.getName(), s.getDescription()))
                .toList();

        List<com.saas.legit.module.identity.dto.PublicProfileResponse.ScheduleDTO> scheduleDTOs = profile.getSchedules().stream()
                .map(s -> new com.saas.legit.module.identity.dto.PublicProfileResponse.ScheduleDTO(
                        s.getDayOfWeek(),
                        s.getStartTime().toString(),
                        s.getEndTime().toString()))
                .toList();

        return new com.saas.legit.module.identity.dto.PublicProfileResponse(
                user.getFirstName() + " " + user.getLastNameFather() + " " + user.getLastNameMother(),
                user.getAvatarURL(),
                profile.getBioLawyer(),
                profile.getCity(),
                profile.getCountry(),
                profile.getHourlyRate(),
                profile.getCurrency(),
                profile.getBarAssociation(),
                profile.getBarRegistrationNumber(),
                lawFirm != null ? lawFirm.getNameLawFirm() : null,
                lawFirm != null ? lawFirm.getLogoUrl() : null,
                lawFirm != null ? lawFirm.getCoverPhotoUrl() : null,
                specialtyDTOs,
                scheduleDTOs
        );
    }
}
