package com.example.dears;

import com.example.dears.data.model.Pet;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JournalLogic {
    Pet pet;
    int userId;
    int timesChatted;
    int timesFed;
    int timesSleep;
    String day;

    public JournalLogic() {}
    public JournalLogic(Pet pet, int userId, int timesChatted, int timesFed, int timesSleep, String day) {
        this.pet = pet;
        this.userId = userId;
        this.timesChatted = timesChatted;
        this.timesFed = timesFed;
        this.timesSleep = timesSleep;
        this.day = day;
    }

    // getters + setters
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getTimesChatted() {
        return timesChatted;
    }
    public void setTimesChatted(int timesChatted) {
        this.timesChatted = timesChatted;
    }
    public int getTimesFed() {
        return timesFed;
    }
    public void setTimesFed(int timesFed) {
        this.timesFed = timesFed;
    }
    public int getTimesSleep() {
        return timesSleep;
    }
    public void setTimesSleep(int timesSleep) {
        this.timesSleep = timesSleep;
    }
    public String getDay() {
        return day;
    }
    public void setDay(String day) {
        this.day = day;
    }

    // write journal entry
    public String writeEntry(String day, String summary, int timesChatted, int timesFed, int timesSleep) {
        String response = day + "\n";
        response += summary;
        if (timesChatted != 0) response += " You chatted with me " + timesChatted + " time" + ((timesChatted == 1) ? "!" : "s!");
        if (timesFed != 0) response += " You fed me " + timesFed + " time" + ((timesFed == 1) ? "!" : "s!") ;
        if (timesSleep != 0) response += " I napped " + timesSleep + " time" + ((timesSleep == 1) ? "!" : "s!") ;

        return response;
    }

    // create key to get image
    public String getPetImageKey() {
        String ageStage = pet.getAge().getAgeStage();
        String type = pet.getType().toLowerCase();

        if (type != "deer" && type != "bear") {
            return "err";
        } else if (ageStage != "baby" && ageStage != "teen" && ageStage != "adult") {
            return "err";
        }

        return ageStage + "_" + type.toLowerCase() + "_default";
    }
}
