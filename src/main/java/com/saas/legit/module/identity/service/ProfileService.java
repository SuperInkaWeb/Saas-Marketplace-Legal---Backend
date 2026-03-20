package com.saas.legit.module.identity.service;

import com.saas.legit.core.service.CloudinaryService;

import com.saas.legit.module.identity.dto.PublicProfileResponse;
import com.saas.legit.module.client.dto.UpdateClientProfileRequest;
import com.saas.legit.module.marketplace.dto.UpdateLawyerProfileRequest;
import com.saas.legit.module.identity.model.ClientProfile;
import com.saas.legit.module.identity.model.User;
import com.saas.legit.module.identity.repository.ClientProfileRepository;
import com.saas.legit.module.identity.repository.UserRepository;
import com.saas.legit.module.marketplace.exception.LawyerProfileNotFoundException;
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
                .orElseThrow(LawyerProfileNotFoundException::new);

        updateUserBasicInfo(user, request.firstName(), request.lastNameFather(), request.lastNameMother(), request.phoneNumber());

        profile.setBioLawyer(request.bio());
        profile.setCity(request.city());
        profile.setCountry(request.country());
        profile.setLatitude(request.latitude());
        profile.setLongitude(request.longitude());
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

    @Transactional(readOnly = true)
    public PublicProfileResponse getPublicProfile(String slug) {
        LawyerProfile profile = lawyerProfileRepository.findBySlug(slug)
                .orElseThrow(LawyerProfileNotFoundException::new);

        User user = profile.getUser();

        List<PublicProfileResponse.SpecialtyDTO> specialtyDTOs = profile.getSpecialties().stream()
                .map(s -> new PublicProfileResponse.SpecialtyDTO(s.getName(), s.getDescription()))
                .toList();

        List<PublicProfileResponse.ScheduleDTO> scheduleDTOs = profile.getSchedules().stream()
                .map(s -> new PublicProfileResponse.ScheduleDTO(
                        s.getDayOfWeek(),
                        s.getStartTime().toString(),
                        s.getEndTime().toString()))
                .toList();

        return new PublicProfileResponse(
                user.getFirstName() + " " + user.getLastNameFather() + " " + user.getLastNameMother(),
                user.getAvatarURL(),
                profile.getBioLawyer(),
                profile.getCity(),
                profile.getCountry(),
                profile.getHourlyRate(),
                profile.getCurrency(),
                profile.getBarAssociation(),
                profile.getBarRegistrationNumber(),
                specialtyDTOs,
                scheduleDTOs
        );
    }
}
