package com.example.dears.data.request;

public class createEntryRequest {
    private String mood;
    private String summary;
    private String petIdStr;
    private String journalIdStr;

    public createEntryRequest(String m, String s, String pid, String jid) {
        mood = m;
        summary = s;
        petIdStr = pid;
        journalIdStr = jid;
    }
}
