-- Add dosage column to treatment table
ALTER TABLE graze.treatment ADD COLUMN dosage VARCHAR(255);

-- Update dosage for each treatment
UPDATE graze.treatment SET dosage = '2 ml subcutaneous (SQ)' WHERE name = 'CDT vaccine (Clostridium perfringens types C & D + Tetanus)';
UPDATE graze.treatment SET dosage = '2 ml subcutaneous (SQ)' WHERE name = 'CDT booster for kids';
UPDATE graze.treatment SET dosage = 'Per body weight as per product label (oral drench)' WHERE name = 'Deworming (internal parasites)';
UPDATE graze.treatment SET dosage = '1 ml per 5 kg body weight (oral)' WHERE name = 'Coccidiosis prevention/treatment';
UPDATE graze.treatment SET dosage = 'Topical pour-on or spray per product label' WHERE name = 'External parasite control (ticks lice mites)';
UPDATE graze.treatment SET dosage = 'Free-choice mineral block or 10–15 g daily in feed' WHERE name = 'Vitamin and mineral supplement';
UPDATE graze.treatment SET dosage = '1 ml per 45 kg body weight (SQ or IM)' WHERE name = 'Selenium and Vitamin E supplement';
UPDATE graze.treatment SET dosage = '1–2 ml intramuscular (IM)' WHERE name = 'Multivitamin injection during stress';
UPDATE graze.treatment SET dosage = 'N/A – manual hoof trimming' WHERE name = 'Foot trimming';
UPDATE graze.treatment SET dosage = 'Topical zinc sulfate solution or copper sulfate foot bath' WHERE name = 'Foot rot inspection and treatment';
UPDATE graze.treatment SET dosage = '2 ml subcutaneous (SQ)' WHERE name = 'Pneumonia vaccination';
UPDATE graze.treatment SET dosage = '2 ml subcutaneous (SQ) – single dose for does 4–8 months' WHERE name = 'Brucellosis vaccination';
UPDATE graze.treatment SET dosage = '2 ml subcutaneous (SQ)' WHERE name = 'Pasteurella vaccination';
UPDATE graze.treatment SET dosage = '1 ml subcutaneous (SQ)' WHERE name = 'Anthrax vaccination (risk areas)';
UPDATE graze.treatment SET dosage = '1 ml subcutaneous (SQ)' WHERE name = 'Rift Valley Fever vaccination (risk areas)';
UPDATE graze.treatment SET dosage = 'N/A – visual inspection and tick removal' WHERE name = 'Tick borne disease inspection';
UPDATE graze.treatment SET dosage = 'N/A – visual inspection; topical antiseptic as needed' WHERE name = 'Wound and injury inspection';
UPDATE graze.treatment SET dosage = 'N/A – palpation and visual check; intramammary antibiotic if infected' WHERE name = 'Mastitis check for lactating does';
UPDATE graze.treatment SET dosage = 'N/A – body condition score 1–5 assessment' WHERE name = 'Body condition scoring check';
UPDATE graze.treatment SET dosage = 'N/A – ultrasound or manual palpation' WHERE name = 'Pregnancy diagnosis check';
UPDATE graze.treatment SET dosage = 'Supplemental grain 200–400 g/day; calcium supplement as needed' WHERE name = 'Pregnancy nutrition check (late pregnancy)';
UPDATE graze.treatment SET dosage = 'N/A – general health assessment and birthing area prep' WHERE name = 'Kidding preparation health check';
UPDATE graze.treatment SET dosage = 'N/A – monitor temperature, appetite, and discharge' WHERE name = 'Post-kidding health check';
UPDATE graze.treatment SET dosage = 'Dip navel in 7% iodine solution' WHERE name = 'Newborn kid navel iodine treatment';
UPDATE graze.treatment SET dosage = '0.25–0.5 ml vitamin A, D, E injection (IM)' WHERE name = 'Newborn kid vitamin injection';
UPDATE graze.treatment SET dosage = '0.25 ml selenium/vitamin E (SQ) or per product label' WHERE name = 'Newborn kid selenium supplement';
UPDATE graze.treatment SET dosage = 'Minimum 50 ml colostrum per kg body weight within first 6 hours' WHERE name = 'Newborn kid colostrum check';
UPDATE graze.treatment SET dosage = 'N/A – cautery or paste method at 3–7 days old' WHERE name = 'Disbudding (if done)';
UPDATE graze.treatment SET dosage = 'N/A – visual wound inspection; antiseptic application' WHERE name = 'Castration check for male kids';
UPDATE graze.treatment SET dosage = 'N/A – weigh and record; adjust feed ration accordingly' WHERE name = 'Growth and weight monitoring';
UPDATE graze.treatment SET dosage = 'Topical antiseptic; systemic antibiotic per vet prescription' WHERE name = 'Hoof abscess treatment check';
UPDATE graze.treatment SET dosage = 'Ophthalmic antibiotic ointment or spray per product label' WHERE name = 'Eye infection treatment check (pink eye)';
UPDATE graze.treatment SET dosage = 'N/A – collect fecal sample; lab analysis for EPG count' WHERE name = 'Internal parasite fecal egg count test';
UPDATE graze.treatment SET dosage = 'N/A – pH and microbial testing of water source' WHERE name = 'Water quality inspection';
UPDATE graze.treatment SET dosage = 'N/A – nutritional analysis of feed composition' WHERE name = 'Feed nutrition inspection';
UPDATE graze.treatment SET dosage = 'N/A – provide shade, ventilation, and fresh water access' WHERE name = 'Heat stress monitoring';
UPDATE graze.treatment SET dosage = 'N/A – provide shelter, bedding, and windbreak' WHERE name = 'Cold stress monitoring';
UPDATE graze.treatment SET dosage = 'N/A – semen evaluation and physical exam by vet' WHERE name = 'Breeding buck fertility check';
UPDATE graze.treatment SET dosage = 'N/A – body condition, vaccination, and deworming pre-check' WHERE name = 'Breeding health check before mating';
UPDATE graze.treatment SET dosage = 'Broad-spectrum dewormer per body weight + CDT vaccine 2 ml SQ' WHERE name = 'Quarantine treatment for new goats';
UPDATE graze.treatment SET dosage = 'N/A – monitor temperature, appetite, and symptoms daily' WHERE name = 'Isolation monitoring for sick animals';
UPDATE graze.treatment SET dosage = 'N/A – review and update all vaccination records' WHERE name = 'Vaccination record review';
UPDATE graze.treatment SET dosage = 'N/A – full herd walk-through and health assessment' WHERE name = 'General herd health inspection';

