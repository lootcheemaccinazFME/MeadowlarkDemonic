package com.flymaccin.meadowlarkdemonic;

import java.util.*;

/** Canonical MIDI editor over MIDI Assets. No private song, timeline or transport state. */
public final class PianoRoll {
    private final DemonicProject project;
    public PianoRoll(DemonicProject project){this.project=project;}

    public List<MidiEvent> events(String assetId){return midi(assetId).midiEvents();}

    public MidiEvent add(final String assetId,long frame,int status,int data1,int data2){
        final Asset a=midi(assetId);final MidiEvent e=new MidiEvent(frame,status,data1,data2);
        project.history.execute(new UndoHistory.Command(){
            public void apply(){a.addMidiEventInternal(e);}
            public void revert(){a.removeMidiEventInternal(e);}
            public String label(){return "Add MIDI event";}
        });
        return e;
    }

    public MidiEvent move(final String assetId,final MidiEvent event,long frame){
        final Asset a=midi(assetId);final MidiEvent replacement=new MidiEvent(frame,event.status,event.data1,event.data2);
        project.history.execute(new UndoHistory.Command(){
            public void apply(){a.replaceMidiEventInternal(event,replacement);}
            public void revert(){a.replaceMidiEventInternal(replacement,event);}
            public String label(){return "Move MIDI event";}
        });
        return replacement;
    }

    public void remove(final String assetId,final MidiEvent event){
        final Asset a=midi(assetId);
        project.history.execute(new UndoHistory.Command(){
            public void apply(){a.removeMidiEventInternal(event);}
            public void revert(){a.addMidiEventInternal(event);}
            public String label(){return "Remove MIDI event";}
        });
    }

    public static final class Note {
        public final MidiEvent on,off;
        Note(MidiEvent on,MidiEvent off){this.on=on;this.off=off;}
        public long startFrame(){return on.frame;}
        public long durationFrames(){return Math.max(1,off.frame-on.frame);}
        public int pitch(){return on.data1;}
        public int velocity(){return on.data2;}
    }

    public Note addNote(final String assetId,long start,long duration,int pitch,int velocity){
        if(duration<1)throw new IllegalArgumentException("duration");
        final Asset a=midi(assetId);
        final MidiEvent on=new MidiEvent(start,0x90,pitch,velocity);
        final MidiEvent off=new MidiEvent(start+duration,0x80,pitch,0);
        project.history.execute(new UndoHistory.Command(){
            public void apply(){a.addMidiEventInternal(on);a.addMidiEventInternal(off);}
            public void revert(){a.removeMidiEventInternal(on);a.removeMidiEventInternal(off);}
            public String label(){return "Add MIDI note";}
        });
        return new Note(on,off);
    }

    public Note editNote(final String assetId,final Note note,long start,long duration,int pitch,int velocity){
        if(duration<1)throw new IllegalArgumentException("duration");
        final Asset a=midi(assetId);
        final MidiEvent newOn=new MidiEvent(start,0x90,pitch,velocity);
        final MidiEvent newOff=new MidiEvent(start+duration,0x80,pitch,0);
        project.history.execute(new UndoHistory.Command(){
            public void apply(){a.replaceMidiEventInternal(note.on,newOn);a.replaceMidiEventInternal(note.off,newOff);}
            public void revert(){a.replaceMidiEventInternal(newOn,note.on);a.replaceMidiEventInternal(newOff,note.off);}
            public String label(){return "Edit MIDI note";}
        });
        return new Note(newOn,newOff);
    }

    public void removeNote(final String assetId,final Note note){
        final Asset a=midi(assetId);
        project.history.execute(new UndoHistory.Command(){
            public void apply(){a.removeMidiEventInternal(note.on);a.removeMidiEventInternal(note.off);}
            public void revert(){a.addMidiEventInternal(note.on);a.addMidiEventInternal(note.off);}
            public String label(){return "Remove MIDI note";}
        });
    }

    private Asset midi(String id){
        Asset a=project.asset(id);
        if(a==null||a.kind!=Asset.Kind.MIDI)throw new IllegalArgumentException("MIDI asset required");
        return a;
    }
}
