package com.example.dears.data.request;

public class createEntryRequest {
    private String mood;
    private String summary;
    private String pet_id;
    private String journal_id;

    public createEntryRequest(String m, String s, String pid, String jid) {
        mood = m;
        summary = s;
        pet_id = pid;
        journal_id = jid;
    }
}
