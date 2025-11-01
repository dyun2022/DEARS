package com.app.project.service.impl;

import com.app.project.model.Entry;
import com.app.project.model.Journal;
import com.app.project.repository.EntryRepository;
import com.app.project.repository.JournalRepository;
import com.app.project.service.JournalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class JournalServiceImpl implements JournalService {
    @Autowired
    private JournalRepository journalRepository;
    @Autowired
    private EntryRepository entryRepository;

    @Override
    public Journal createJournal() {
        return null;
    }

    @Override
    public Journal getJournalById(int journal_id) {
        return null;
    }

    @Override
    public ArrayList<Entry> getAllEntries() {
        return null;
    }

    @Override
    public Entry getEntrybyId(int entry_id) {
        return null;
    }

    @Override
    public Journal getJournalByDate(Date date) {
        return null;
    }
}