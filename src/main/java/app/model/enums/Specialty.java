package app.model.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Specialty {

    ACUPUNCTURE(41, "Acupuncture"),
    OBSTETRICIAN_GYNECOLOGIST(1, "Obstetrician-Gynecologist"),
    MIDWIFE(80, "Midwife"),
    ALLERGIST(2, "Allergist"),
    ALTERNATIVE_MEDICINE(83, "Alternative Medicine"),
    ANGIOLOGIST(52, "Angiologist"),
    ANESTHESIOLOGIST(56, "Anesthesiologist"),
    AYURVEDA(75, "Ayurveda"),
    BOWEN_THERAPIST(58, "Bowen Therapist"),
    VIROLOGIST(69, "Virologist"),
    INTERNAL_MEDICINE(16, "Internal Medicine"),
    GASTROENTEROLOGIST(3, "Gastroenterologist"),
    THORACIC_SURGEON(30, "Thoracic Surgeon"),
    DERMATOLOGIST(4, "Dermatologist"),
    PEDIATRIC_GASTROENTEROLOGIST(49, "Pediatric Gastroenterologist"),
    PEDIATRIC_ENDOCRINOLOGIST(36, "Pediatric Endocrinologist"),
    PEDIATRIC_CARDIOLOGIST(48, "Pediatric Cardiologist"),
    PEDIATRIC_NEUROLOGIST(23, "Pediatric Neurologist"),
    PEDIATRIC_NEPHROLOGIST(31, "Pediatric Nephrologist"),
    CHILD_PSYCHIATRIST(39, "Child Psychiatrist"),
    PEDIATRIC_PULMONOLOGIST(33, "Pediatric Pulmonologist"),
    PEDIATRIC_RHEUMATOLOGIST(51, "Pediatric Rheumatologist"),
    PEDIATRIC_HEMATOLOGIST(65, "Pediatric Hematologist"),
    PEDIATRIC_SURGEON(35, "Pediatric Surgeon"),
    DIETITIAN(32, "Dietitian"),
    EMBRYOLOGIST(91, "Embryologist"),
    ENDODONTIST(86, "Endodontist"),
    ENDOCRINOLOGIST(6, "Endocrinologist"),
    AESTHETIC_MEDICINE(5, "Aesthetic Medicine"),
    DENTIST(7, "Dentist"),
    EXAMINATION(64, "Examination"),
    IMPLANTOLOGIST(85, "Implantologist"),
    IMMUNOLOGIST(68, "Immunologist"),
    INFECTIOUS_DISEASES(55, "Infectious Diseases"),
    CARDIOLOGIST(8, "Cardiologist"),
    CARDIAC_SURGEON(53, "Cardiac Surgeon"),
    KINESIOTHERAPIST(57, "Kinesiotherapist"),
    CLINICAL_LABORATORY(70, "Clinical Laboratory"),
    COACH(79, "Coach"),
    MAXILLOFACIAL_SURGEON(54, "Maxillofacial Surgeon"),
    SPEECH_THERAPIST(38, "Speech Therapist"),
    RADIATION_THERAPIST(81, "Radiation Therapist"),
    MAMMOLOGIST(50, "Mammologist"),
    MANIPULATION(78, "Manipulation"),
    MEDICAL_GENETICS(24, "Medical Genetics"),
    NURSE(88, "Nurse"),
    MEDICAL_COSMETOLOGIST(93, "Medical Cosmetologist"),
    MICROBIOLOGIST(67, "Microbiologist"),
    NEUROLOGIST(10, "Neurologist"),
    NEUROSURGEON(37, "Neurosurgeon"),
    NEONATOLOGIST(40, "Neonatologist"),
    NEPHROLOGIST(11, "Nephrologist"),
    NUCLEAR_MEDICINE(72, "Nuclear Medicine"),
    NUTRITIONIST(95, "Nutritionist"),
    IMAGING_DIAGNOSTICS(43, "Imaging Diagnostics"),
    GENERAL_PRACTITIONER(9, "General Practitioner"),
    OZONE_THERAPIST(60, "Ozone Therapist"),
    ONCOLOGIST(42, "Oncologist"),
    ORAL_SURGEON(76, "Oral Surgeon"),
    ORTHODONTIST(61, "Orthodontist"),
    ORTHOPEDIST(12, "Orthopedist"),
    OTONEUROLOGIST(19, "Otoneurologist"),
    OPHTHALMOLOGIST(13, "Ophthalmologist"),
    PARASITOLOGIST(59, "Parasitologist"),
    PERIODONTIST(87, "Periodontist"),
    PEDIATRICIAN(14, "Pediatrician"),
    PLASTIC_SURGEON(45, "Plastic Surgeon"),
    PODIATRIST(89, "Podiatrist"),
    PRENATAL_POSTNATAL_CARE(92, "Prenatal/Postnatal Care"),
    PROSTHETIST(84, "Prosthetist"),
    PREVENTIVE_EXAMINATIONS(25, "Preventive Examinations"),
    PSYCHIATRIST(28, "Psychiatrist"),
    PSYCHOLOGIST(34, "Psychologist"),
    PSYCHOTHERAPIST(46, "Psychotherapist"),
    PULMONOLOGIST(15, "Pulmonologist"),
    RHEUMATOLOGIST(44, "Rheumatologist"),
    REPRODUCTIVE_MEDICINE(66, "Reproductive Medicine"),
    REHABILITATION_SPECIALIST(74, "Rehabilitation Specialist"),
    SLEEP_MEDICINE_SPECIALIST(94, "Sleep Medicine Specialist"),
    SPORTS_MEDICINE(47, "Sports Medicine"),
    VASCULAR_SURGEON(26, "Vascular Surgeon"),
    TOXICOLOGIST(73, "Toxicologist"),
    ENT(17, "ENT (Ear, Nose, Throat)"),
    UROLOGIST(20, "Urologist"),
    PHYSIOTHERAPIST(22, "Physiotherapist"),
    HEMATOLOGIST(27, "Hematologist"),
    TRANSFUSION_HEMATOLOGIST(71, "Transfusion Hematologist"),
    SURGEON(21, "Surgeon"),
    HOMEOPATH(18, "Homeopath"),
    YUMEIHO_THERAPIST(82, "Yumeiho Therapist");

    private final int id;
    private final String englishName;

    Specialty(int id, String englishName) {
        this.id = id;
        this.englishName = englishName;
    }

    public static Specialty fromId(int id) {
        return Arrays.stream(values())
                .filter(s -> s.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown specialty id: " + id));
    }

    public static Specialty fromEnglishName(String name) {
        if (name == null) return null;
        return Arrays.stream(values())
                .filter(s -> s.englishName.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
