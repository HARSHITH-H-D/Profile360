package com.example.staffprofile;

public class Profile {
    private String name;
    private String jobTitle;
    private String skills;
    private String certifications;
    private String photoPath;

    public Profile(String name, String jobTitle, String skills, String certifications, String photoPath) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.skills = skills;
        this.certifications = certifications;
        this.photoPath = photoPath;
    }

    public String getName() {
        return name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getSkills() {
        return skills;
    }

    public String getCertifications() {
        return certifications;
    }

    public String getPhotoPath() {
        return photoPath;
    }
}
