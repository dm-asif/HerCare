package com.example.localization

object HerCareStrings {

    // App Branding & Navigation
    fun appName(lang: AppLanguage) = "HerCare"

    fun appTagline(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Empowering Women's Health & Cycle Care"
        AppLanguage.BANGLA -> "নারীদের স্বাস্থ্য ও পিরিয়ড সুরক্ষার বিশ্বস্ত সঙ্গী"
    }

    fun navHome(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Cycle"
        AppLanguage.BANGLA -> "সাইকেল"
    }

    fun navCalendar(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Calendar"
        AppLanguage.BANGLA -> "ক্যালেন্ডার"
    }

    fun navDischarge(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Discharge"
        AppLanguage.BANGLA -> "স্রাব গাইড"
    }

    fun navMedsAndStock(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Meds & Stock"
        AppLanguage.BANGLA -> "ওষুধ ও প্যাড"
    }

    fun navReport(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Report"
        AppLanguage.BANGLA -> "রিপোর্ট"
    }

    fun navSettings(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Settings"
        AppLanguage.BANGLA -> "সেটিংস"
    }

    // Onboarding
    fun welcomeTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Welcome to HerCare"
        AppLanguage.BANGLA -> "HerCare-এ আপনাকে স্বাগতম"
    }

    fun onboardingSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Private, offline-first health tracking and medical guidance tailored for you."
        AppLanguage.BANGLA -> "সম্পূর্ণ নিরাপদ, অফলাইন ট্র্যাকিং ও নির্ভরযোগ্য স্বাস্থ্য নির্দেশিকা।"
    }

    fun chooseLanguage(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Choose Your Language / ভাষা নির্বাচন করুন"
        AppLanguage.BANGLA -> "পছন্দের ভাষা নির্বাচন করুন"
    }

    fun enterName(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Your Name"
        AppLanguage.BANGLA -> "আপনার নাম"
    }

    fun dateOfBirth(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Date of Birth"
        AppLanguage.BANGLA -> "জন্মতারিখ"
    }

    fun lastPeriodStartDate(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Last Period Start Date"
        AppLanguage.BANGLA -> "সর্বশেষ পিরিয়ড শুরুর তারিখ"
    }

    fun selectOlderDateHint(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "You can select today or any past date from the calendar."
        AppLanguage.BANGLA -> "আপনি ক্যালেন্ডার থেকে যেকোনো পূর্ববর্তী তারিখ বেছে নিতে পারেন।"
    }

    fun averageCycleLength(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Average Cycle Length (Days)"
        AppLanguage.BANGLA -> "গড় সাইকেল দৈর্ঘ্য (দিন)"
    }

    fun cycleLengthHint(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Commonly 21 to 35 days (Normal: ~28 days)"
        AppLanguage.BANGLA -> "সাধারণত ২১ থেকে ৩৫ দিন (স্বাভাবিক: প্রায় ২৮ দিন)"
    }

    fun averagePeriodDuration(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Average Period Duration (Days)"
        AppLanguage.BANGLA -> "পিরিয়ডের স্থায়িত্বকাল (দিন)"
    }

    fun periodDurationHint(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Commonly 2 to 7 days (Normal: ~5 days)"
        AppLanguage.BANGLA -> "সাধারণত ২ থেকে ৭ দিন (স্বাভাবিক: প্রায় ৫ দিন)"
    }

    fun initialPadsCount(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Available Pads at Home"
        AppLanguage.BANGLA -> "ঘরে মজুত প্যাডের সংখ্যা"
    }

    fun getStarted(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Get Started"
        AppLanguage.BANGLA -> "শুরু করুন"
    }

    // Home / Cycle Dashboard
    fun cycleDay(lang: AppLanguage, day: Int) = when (lang) {
        AppLanguage.ENGLISH -> "Cycle Day $day"
        AppLanguage.BANGLA -> "সাইকেল দিন $day"
    }

    fun daysUntilPeriod(lang: AppLanguage, days: Int) = when (lang) {
        AppLanguage.ENGLISH -> if (days > 0) "$days days until next period" else if (days == 0) "Period expected today" else "Period is ${-days} days late"
        AppLanguage.BANGLA -> if (days > 0) "পরবর্তী পিরিয়ড হতে বাকি $days দিন" else if (days == 0) "আজ পিরিয়ড শুরু হতে পারে" else "পিরিয়ড ${-days} দিন বিলম্বিত"
    }

    fun expectedOn(lang: AppLanguage, dateStr: String) = when (lang) {
        AppLanguage.ENGLISH -> "Expected around: $dateStr"
        AppLanguage.BANGLA -> "প্রত্যাশিত তারিখ: $dateStr"
    }

    fun phaseMenstrual(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Menstrual Phase"
        AppLanguage.BANGLA -> "মেনস্ট্রুয়াল পর্যায় (রক্তস্রাব)"
    }

    fun phaseFollicular(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Follicular Phase"
        AppLanguage.BANGLA -> "ফলিকুলার পর্যায়"
    }

    fun phaseFertile(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Fertile Window & Ovulation"
        AppLanguage.BANGLA -> "উর্বর সময় ও ওভুলেশন"
    }

    fun phaseLuteal(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Luteal Phase"
        AppLanguage.BANGLA -> "লুটিয়াল পর্যায়"
    }

    fun phaseDescMenstrual(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Your uterine lining is shedding. Rest well and maintain good hydration."
        AppLanguage.BANGLA -> "জরায়ুর আস্তরণ ঝরে পড়ছে। বিশ্রাম নিন এবং পর্যাপ্ত পানি পান করুন।"
    }

    fun phaseDescFollicular(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Estrogen rises as follicles mature. Energy levels typically increase."
        AppLanguage.BANGLA -> "ইস্ট্রোজেনের মাত্রা বাড়ে। কর্মশক্তি ও সতেজতা বৃদ্ধি পায়।"
    }

    fun phaseDescFertile(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "High probability of conception. Cervical fluid may become slippery and clear."
        AppLanguage.BANGLA -> "গর্ভধারণের সর্বোচ্চ সম্ভাবনাময় সময়। স্রাব সাধারণত স্বচ্ছ ও পিচ্ছিল হয়।"
    }

    fun phaseDescLuteal(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Progesterone rises. You may experience mild premenstrual symptoms (PMS)."
        AppLanguage.BANGLA -> "প্রোজেস্টেরন বৃদ্ধি পায়। হালকা পিএমএস উপসর্গ দেখা দিতে পারে।"
    }

    fun quickLogToday(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Log Today's Flow & Symptoms"
        AppLanguage.BANGLA -> "আজকের রক্তপ্রবাহ ও উপসর্গ যোগ করুন"
    }

    fun periodActiveBanner(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Period in Progress"
        AppLanguage.BANGLA -> "পিরিয়ড চলমান"
    }

    fun logPeriodStart(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Period Started"
        AppLanguage.BANGLA -> "পিরিয়ড শুরু"
    }

    fun logPeriodEnd(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Period Ended"
        AppLanguage.BANGLA -> "পিরিয়ড শেষ"
    }

    // Flow Levels
    fun flowNone(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "No Flow"
        AppLanguage.BANGLA -> "রক্তপ্রবাহ নেই"
    }

    fun flowLight(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Light"
        AppLanguage.BANGLA -> "হালকা"
    }

    fun flowMedium(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Medium"
        AppLanguage.BANGLA -> "মাঝারি"
    }

    fun flowHeavy(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Heavy"
        AppLanguage.BANGLA -> "ভারী"
    }

    fun flowVeryHeavy(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Very Heavy"
        AppLanguage.BANGLA -> "খুব ভারী"
    }

    // Pain Levels
    fun painNone(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "No Pain"
        AppLanguage.BANGLA -> "কোন ব্যথা নেই"
    }

    fun painMild(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Mild Pain"
        AppLanguage.BANGLA -> "হালকা ব্যথা"
    }

    fun painModerate(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Moderate Pain"
        AppLanguage.BANGLA -> "মাঝারি ব্যথা"
    }

    fun painSevere(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Severe Pain"
        AppLanguage.BANGLA -> "তীব্র ব্যথা"
    }

    // Symptoms
    fun symptomCramps(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Cramps"
        AppLanguage.BANGLA -> "পেটে টান/খিঁচুনি"
    }

    fun symptomHeadache(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Headache"
        AppLanguage.BANGLA -> "মাথাব্যথা"
    }

    fun symptomBloating(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Bloating"
        AppLanguage.BANGLA -> "পেট ফাঁপা"
    }

    fun symptomBreastTenderness(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Breast Tenderness"
        AppLanguage.BANGLA -> "বুকে স্পর্শকাতরতা"
    }

    fun symptomFatigue(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Fatigue / Tiredness"
        AppLanguage.BANGLA -> "ক্লান্তি ও দুর্বলতা"
    }

    fun symptomMoodSwings(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Mood Swings"
        AppLanguage.BANGLA -> "মেজাজ পরিবর্তন"
    }

    fun symptomBackache(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Backache"
        AppLanguage.BANGLA -> "কোমর/পিঠ ব্যথা"
    }

    fun symptomAcne(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Acne / Breakouts"
        AppLanguage.BANGLA -> "ব্রণ"
    }

    fun symptomNausea(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Nausea"
        AppLanguage.BANGLA -> "বমি বমি ভাব"
    }

    // Irregular Cycle Warnings
    fun irregularWarningTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Irregular Cycle Pattern Warning"
        AppLanguage.BANGLA -> "অনিয়মিত সাইকেল সতর্কতা"
    }

    fun shortCycleWarning(lang: AppLanguage, length: Int) = when (lang) {
        AppLanguage.ENGLISH -> "Cycle length is $length days (shorter than the normal range of 21–35 days)."
        AppLanguage.BANGLA -> "সাইকেল দৈর্ঘ্য $length দিন (স্বাভাবিক ২১–৩৫ দিনের চেয়ে কম)।"
    }

    fun longCycleWarning(lang: AppLanguage, length: Int) = when (lang) {
        AppLanguage.ENGLISH -> "Cycle length is $length days (longer than the normal range of 21–35 days)."
        AppLanguage.BANGLA -> "সাইকেল দৈর্ঘ্য $length দিন (স্বাভাবিক ২১–৩৫ দিনের চেয়ে বেশি)।"
    }

    fun variableCycleWarning(lang: AppLanguage, variation: Int) = when (lang) {
        AppLanguage.ENGLISH -> "High cycle variability ($variation days variation between recent cycles)."
        AppLanguage.BANGLA -> "সাইকেলের ধরনে তীব্র তারতম্য (সাম্প্রতিক সাইকেলে $variation দিনের ব্যবধান)।"
    }

    fun prolongedBleedingWarning(lang: AppLanguage, days: Int) = when (lang) {
        AppLanguage.ENGLISH -> "Bleeding prolonged for $days days (normal duration is 2–7 days)."
        AppLanguage.BANGLA -> "রক্তস্রাব $days দিন ধরে চলছে (স্বাভাবিক সময়সীমা ২–৭ দিন)।"
    }

    fun heavyBleedingWarning(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Consecutive very heavy flow days recorded."
        AppLanguage.BANGLA -> "টানা একাধিক দিন অতি ভারী রক্তপ্রবাহ দেখা গেছে।"
    }

    fun consultDoctorNotice(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "If this pattern continues, please consult a qualified gynecologist or healthcare professional for proper evaluation."
        AppLanguage.BANGLA -> "এই অনিয়ম অব্যাহত থাকলে অনুগ্রহ করে একজন অভিজ্ঞ স্ত্রীরোগ বিশেষজ্ঞ (গাইনোকোলজিস্ট) বা চিকিৎসকের পরামর্শ নিন।"
    }

    fun nonDiagnosisDisclaimer(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "HerCare provides observational tracking only and never diagnoses any disease or medical condition."
        AppLanguage.BANGLA -> "HerCare শুধুমাত্র পর্যবেক্ষণমূলক ট্র্যাকিং প্রদান করে এবং কোনো রোগ নির্ণয় বা প্রেসক্রিপশন দেয় না।"
    }

    // Medicine Routine
    fun medicineTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Medicine Routine"
        AppLanguage.BANGLA -> "ওষুধের রুটিন"
    }

    fun addMedicine(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Add Medicine"
        AppLanguage.BANGLA -> "ওষুধ যোগ করুন"
    }

    fun medicineName(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Medicine Name"
        AppLanguage.BANGLA -> "ওষুধের নাম"
    }

    fun medicineDose(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Dose (e.g., 500mg, 1 tablet)"
        AppLanguage.BANGLA -> "ডোজ (যেমন: ৫০০মিগ্রা, ১টি ট্যাবলেট)"
    }

    fun medicineTime(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Reminder Time"
        AppLanguage.BANGLA -> "রিমাইন্ডারের সময়"
    }

    fun medicineFrequency(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Frequency"
        AppLanguage.BANGLA -> "গ্রহণের পুনরাবৃত্তি"
    }

    fun freqDaily(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "1 time / day"
        AppLanguage.BANGLA -> "দিনে ১ বার"
    }

    fun freqTwiceDaily(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "2 times / day"
        AppLanguage.BANGLA -> "দিনে ২ বার"
    }

    fun freqThreePerDay(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "3 times / day"
        AppLanguage.BANGLA -> "দিনে ৩ বার"
    }

    fun freqFourPerDay(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "4 times / day"
        AppLanguage.BANGLA -> "দিনে ৪ বার"
    }

    fun freqEvery8Hours(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Every 8 Hours"
        AppLanguage.BANGLA -> "প্রতি ৮ ঘণ্টা"
    }

    fun freqAsNeeded(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "As Needed (SOS)"
        AppLanguage.BANGLA -> "প্রয়োজন অনুযায়ী"
    }

    fun mealTimingLabel(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Meal Timing"
        AppLanguage.BANGLA -> "খাবারের সাথে সময়"
    }

    fun mealBefore(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Before meal"
        AppLanguage.BANGLA -> "খাবারের আগে"
    }

    fun mealAfter(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "After meal"
        AppLanguage.BANGLA -> "খাবারের পরে"
    }

    fun mealWith(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "With meal"
        AppLanguage.BANGLA -> "খাবারের সাথে"
    }

    fun mealAny(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Any time"
        AppLanguage.BANGLA -> "যেকোনো সময়"
    }

    fun customReminderTimes(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Dose Times (tap to edit)"
        AppLanguage.BANGLA -> "ডোজের সময় (পরিবর্তনে চাপুন)"
    }

    fun defaultTimesSuggestionNote(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "These are only convenient default time suggestions, NOT medical instructions."
        AppLanguage.BANGLA -> "এগুলো কেবল সুবিধাজনক সম্ভাব্য সময়ের প্রস্তাবনা, কোনো চিকিৎসাগত নির্দেশ বা প্রেসক্রিপশন নয়।"
    }

    fun medicineStartDate(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Start Date"
        AppLanguage.BANGLA -> "শুরুর তারিখ"
    }

    fun medicineEndDate(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "End Date"
        AppLanguage.BANGLA -> "শেষের তারিখ"
    }

    fun markTakenToday(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Taken Today"
        AppLanguage.BANGLA -> "আজ গ্রহণ করা হয়েছে"
    }

    fun medicineDisclaimer(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "HerCare never recommends or prescribes medicine. Reminders follow your schedule entered from your doctor's prescription."
        AppLanguage.BANGLA -> "HerCare কখনোই ওষুধ প্রেসক্রাইব বা সুপারিশ করে না। এটি কেবল আপনার চিকিৎসকের প্রেসক্রিপশন অনুযায়ী প্রবেশ করানো সময়ে রিমাইন্ডার দেয়।"
    }

    // Vaginal Discharge Guide
    fun dischargeGuideTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Vaginal Discharge Guide"
        AppLanguage.BANGLA -> "যোনিস্রাব নির্দেশিকা"
    }

    fun normalDischargeHeader(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Normal & Healthy Discharge"
        AppLanguage.BANGLA -> "স্বাভাবিক ও সুস্থ স্রাবের লক্ষণ"
    }

    fun normalDischargeBody(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Normal vaginal discharge is commonly clear or white, without a strong unpleasant or fishy smell. Depending on your cycle phase, it may be thick and sticky (early follicular/late luteal) or slippery, stretchy, and wet like raw egg whites (around ovulation). This is the body's natural self-cleaning and fertility mechanism."
        AppLanguage.BANGLA -> "স্বাভাবিক যোনিস্রাব সাধারণত পরিষ্কার বা সাদা রঙের হয় এবং এতে কোনো তীব্র দুর্গন্ধ থাকে না। সাইকেলের পর্যায়ের ওপর ভিত্তি করে এটি কখনো ঘন ও আঠালো (পিরিয়ডের ঠিক পর বা আগে) অথবা কখনো কাঁচা ডিমের সাদা অংশের মতো পিচ্ছিল, ভেজা ও প্রসারণযোগ্য (ওভুলেশনের সময়) হতে পারে। এটি জরায়ুর প্রাকৃতিক আত্মপরিচ্ছন্নতা ও প্রজনন প্রক্রিয়ার অংশ।"
    }

    fun concerningDischargeHeader(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Concerning Signs & Doctor Warning"
        AppLanguage.BANGLA -> "সতর্কতামূলক লক্ষণ ও জরুরি পরামর্শ"
    }

    fun concerningDischargeWarning(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Consult a doctor or healthcare professional promptly if you experience:\n• Unusual color: Yellow, green, grayish, or frothy discharge\n• Strong, foul, or fishy odor\n• Persistent vaginal itching, soreness, redness, or burning\n• Pelvic or lower abdominal pain\n• Pain or burning while urinating\n• Unusual bleeding or spotting between periods or after intercourse"
        AppLanguage.BANGLA -> "নিম্নলিখিত লক্ষণগুলো দেখা দিলে অবিলম্বে অভিজ্ঞ চিকিৎসকের পরামর্শ নিন:\n• অস্বাভাবিক রঙ: হলুদ, সবুজ, ধূসর বা ফেনা জাতীয় স্রাব\n• তীব্র বা আঁশটে দুর্গন্ধ\n• তীব্র চুলকানি, জ্বালাপোড়া, লালচে ভাব বা অস্বস্তি\n• তলপেটে বা পেলভিক অঞ্চলে ব্যথা\n• প্রস্রাবের সময় জ্বালাপোড়া বা তীব্র ব্যথা\n• দুই পিরিয়ডের মধ্যবর্তী সময়ে বা সহবাসের পর অনাকাঙ্ক্ষিত রক্তপাত"
    }

    fun dischargeSymptomChecker(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Interactive Discharge Assessment"
        AppLanguage.BANGLA -> "স্রাবের ধরন পর্যালোচনা"
    }

    // Pad Stock Tracker
    fun padStockTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Pad Stock Tracker"
        AppLanguage.BANGLA -> "প্যাড মজুত ট্র্যাকার"
    }

    fun remainingPads(lang: AppLanguage, count: Int) = when (lang) {
        AppLanguage.ENGLISH -> "$count Pads Remaining"
        AppLanguage.BANGLA -> "$count টি প্যাড অবশিষ্ট আছে"
    }

    fun padsUsedToday(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Pads Used Today"
        AppLanguage.BANGLA -> "আজ ব্যবহৃত প্যাড"
    }

    fun logPadUsed(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "+1 Pad Used"
        AppLanguage.BANGLA -> "+১ প্যাড ব্যবহার"
    }

    fun outOfStock(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "🔴 OUT OF STOCK"
        AppLanguage.BANGLA -> "🔴 স্টক শেষ"
    }

    fun restockButton(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "RESTOCK"
        AppLanguage.BANGLA -> "রিস্টক"
    }

    fun restockPads(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Restock / Update Stock"
        AppLanguage.BANGLA -> "নতুন প্যাড মজুত যোগ করুন"
    }

    fun restockDialogPrompt(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Add pads to current stock:"
        AppLanguage.BANGLA -> "মজুতে নতুন প্যাড সংখ্যা যোগ করুন:"
    }

    fun padLowStockNotification(lang: AppLanguage, days: Int, pads: Int) = when (lang) {
        AppLanguage.ENGLISH -> "Low Pad Stock Alert: Your period is expected in $days days, and you have only $pads pads left. Please purchase pads soon."
        AppLanguage.BANGLA -> "প্যাড শেষ হওয়ার সতর্কতা: আর $days দিন পর পিরিয়ড হতে পারে এবং আপনার কাছে মাত্র $pads টি প্যাড আছে। অনুগ্রহ করে দ্রুত নতুন প্যাড কিনে রাখুন।"
    }

    // Personal Health Report
    fun healthReportTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Personal Health Report"
        AppLanguage.BANGLA -> "ব্যক্তিগত স্বাস্থ্য প্রতিবেদন"
    }

    fun healthReportSummary(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Comprehensive summary of your recorded menstrual health, flow history, and patterns."
        AppLanguage.BANGLA -> "আপনার সংরক্ষিত মাসিক স্বাস্থ্য, রক্তপ্রবাহ ও চক্রের সার্বিক পরিসংখ্যান।"
    }

    fun cycleRegularity(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Cycle Regularity Status"
        AppLanguage.BANGLA -> "সাইকেল নিয়মিততার অবস্থা"
    }

    fun statusRegular(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Regular (Within 21–35 days)"
        AppLanguage.BANGLA -> "নিয়মিত (২১–৩৫ দিনের স্বাভাবিক পরিসরে)"
    }

    fun statusIrregular(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Irregular Pattern Detected"
        AppLanguage.BANGLA -> "অনিয়মিত প্যাটার্ন চিহ্নিত"
    }

    // Local Notifications
    fun notifChannelPeriod(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Period & Cycle Reminders"
        AppLanguage.BANGLA -> "পিরিয়ড ও সাইকেল রিমাইন্ডার"
    }

    fun notifChannelMeds(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Medicine Routine Reminders"
        AppLanguage.BANGLA -> "ওষুধের রুটিন রিমাইন্ডার"
    }

    fun notifChannelHealth(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Health & Irregularity Warnings"
        AppLanguage.BANGLA -> "স্বাস্থ্য ও সতর্কতা নোটিফিকেশন"
    }

    fun notifChannelPads(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Pad Purchase Reminders"
        AppLanguage.BANGLA -> "প্যাড কেনার তাগিদ"
    }

    fun testNotificationsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Notification Verification & Testing"
        AppLanguage.BANGLA -> "নোটিফিকেশন যাচাই ও পরীক্ষা"
    }

    fun testNotifMeds(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "💊 Test Medicine Reminder"
        AppLanguage.BANGLA -> "💊 ওষুধ রিমাইন্ডার টেস্ট"
    }

    fun testNotifPeriod(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "🩸 Test Period Reminder"
        AppLanguage.BANGLA -> "🩸 পিরিয়ড রিমাইন্ডার টেস্ট"
    }

    fun testNotifIrregular(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "🔴 Test Irregular-Cycle Warning"
        AppLanguage.BANGLA -> "🔴 অনিয়মিত সাইকেল সতর্কতা টেস্ট"
    }

    fun testNotifPad(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "🧻 Test Pad Purchase Reminder"
        AppLanguage.BANGLA -> "🧻 প্যাড কেনার নোটিফিকেশন টেস্ট"
    }

    fun testNotifHealth(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "⚠️ Test Health Warning"
        AppLanguage.BANGLA -> "⚠️ স্বাস্থ্য সতর্কতা টেস্ট"
    }

    // Biometric Security
    fun biometricTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Biometric Privacy Lock"
        AppLanguage.BANGLA -> "বায়োমেট্রিক নিরাপত্তা লক"
    }

    fun biometricDesc(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Require fingerprint or face unlock to access your private health records."
        AppLanguage.BANGLA -> "আপনার ব্যক্তিগত স্বাস্থ্য তথ্য সুরক্ষিত রাখতে আঙুলের ছাপ বা ফেস আনলক বাধ্যতামূলক করুন।"
    }

    fun unlockAppPrompt(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Unlock HerCare"
        AppLanguage.BANGLA -> "HerCare আনলক করুন"
    }

    fun unlockSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Authenticate to view your private health data"
        AppLanguage.BANGLA -> "আপনার ব্যক্তিগত তথ্য দেখতে যাচাই করুন"
    }

    // Navigation & Tab Aliases
    fun homeTab(lang: AppLanguage) = navHome(lang)
    fun historyTab(lang: AppLanguage) = navCalendar(lang)
    fun dischargeTab(lang: AppLanguage) = navDischarge(lang)
    fun medicineTab(lang: AppLanguage) = navMedsAndStock(lang)
    fun reportTab(lang: AppLanguage) = navReport(lang)
    fun settingsTab(lang: AppLanguage) = navSettings(lang)

    fun medicineRoutineTab(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Medicine Routine"
        AppLanguage.BANGLA -> "ওষুধের রুটিন"
    }

    fun padStockTrackerTab(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Pad Stock Tracker"
        AppLanguage.BANGLA -> "প্যাড স্টক ট্র্যাকার"
    }

    fun settingsTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Settings & Privacy"
        AppLanguage.BANGLA -> "সেটিংস ও নিরাপত্তা"
    }

    fun personalInfoTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Personal Information"
        AppLanguage.BANGLA -> "ব্যক্তিগত তথ্য"
    }

    fun personalInfoSubtitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Name and date of birth used for your cycle health profile"
        AppLanguage.BANGLA -> "আপনার সাইকেল স্বাস্থ্য প্রোফাইলের নাম ও জন্ম তারিখ"
    }

    fun editPersonalInfo(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Edit Information"
        AppLanguage.BANGLA -> "তথ্য পরিবর্তন করুন"
    }

    fun savedSuccessfully(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Changes saved successfully"
        AppLanguage.BANGLA -> "সফলভাবে সংরক্ষিত হয়েছে"
    }

    fun languagePreference(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "App Language / ভাষা"
        AppLanguage.BANGLA -> "অ্যাপের ভাষা (Language)"
    }

    fun biometricDescription(lang: AppLanguage) = biometricDesc(lang)

    fun notificationsSection(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Android Local Notifications"
        AppLanguage.BANGLA -> "অ্যান্ড্রয়েড লোকাল নোটিফিকেশন"
    }

    fun testMedicineNotification(lang: AppLanguage) = testNotifMeds(lang)
    fun testPeriodNotification(lang: AppLanguage) = testNotifPeriod(lang)
    fun testIrregularNotification(lang: AppLanguage) = testNotifIrregular(lang)
    fun testPadNotification(lang: AppLanguage) = testNotifPad(lang)
    fun testHealthNotification(lang: AppLanguage) = testNotifHealth(lang)

    fun cancel(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Cancel"
        AppLanguage.BANGLA -> "বাতিল"
    }

    fun save(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Save"
        AppLanguage.BANGLA -> "সংরক্ষণ"
    }

    fun medicalSourcesTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "Reliable Medical Sources"
        AppLanguage.BANGLA -> "নির্ভরযোগ্য চিকিৎসাগত তথ্যসূত্র"
    }

    fun medicalSourcesText(lang: AppLanguage) = when (lang) {
        AppLanguage.ENGLISH -> "HerCare relies on clinical benchmarks from the American College of Obstetricians and Gynecologists (ACOG), Mayo Clinic, and WHO: menstrual cycles typically range 21–35 days and periods last 2–7 days. HerCare is purely observational and never provides a clinical diagnosis. Always consult a healthcare professional for persistent irregularities."
        AppLanguage.BANGLA -> "HerCare বিশ্ব স্বাস্থ্য সংস্থা (WHO) ও চিকিৎসা বিজ্ঞানের স্বীকৃত নির্দেশিকা অনুযায়ী তৈরি: স্বাভাবিক পিরিয়ড ২–৭ দিন স্থায়ী হয় এবং মাসিক চক্র ২১–৩৫ দিন অন্তর ঘটে। HerCare কেবল পর্যবেক্ষণমূলক পরিসংখ্যান দেয় এবং কোনো রোগ নির্ণয় করে না। অস্বাভাবিকতা অব্যাহত থাকলে বিশেষজ্ঞ চিকিৎসকের পরামর্শ নিন।"
    }
}
