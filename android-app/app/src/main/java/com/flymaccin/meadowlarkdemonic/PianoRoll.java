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

    public List<Note> editNotes(final String assetId,final List<Note> notes,final long deltaFrame,final int deltaPitch){
        if(notes==null||notes.isEmpty())return Collections.emptyList();
        final Asset a=midi(assetId);final ArrayList<Note> replacements=new ArrayList<>();
        for(Note n:notes){
            long start=Math.max(0,n.startFrame()+deltaFrame);int pitch=Math.max(0,Math.min(127,n.pitch()+deltaPitch));
            replacements.add(new Note(new MidiEvent(start,0x90,pitch,n.velocity()),new MidiEvent(start+n.durationFrames(),0x80,pitch,0)));
        }
        project.history.execute(new UndoHistory.Command(){
            public void apply(){for(int i=0;i<notes.size();i++){Note old=notes.get(i),neu=replacements.get(i);a.replaceMidiEventInternal(old.on,neu.on);a.replaceMidiEventInternal(old.off,neu.off);}}
            public void revert(){for(int i=notes.size()-1;i>=0;i--){Note old=notes.get(i),neu=replacements.get(i);a.replaceMidiEventInternal(neu.on,old.on);a.replaceMidiEventInternal(neu.off,old.off);}}
            public String label(){return "Edit MIDI notes";}
        });
        return Collections.unmodifiableList(replacements);
    }

    public void removeNotes(final String assetId,final List<Note> notes){
        if(notes==null||notes.isEmpty())return;final Asset a=midi(assetId);
        project.history.execute(new UndoHistory.Command(){
            public void apply(){for(Note n:notes){a.removeMidiEventInternal(n.on);a.removeMidiEventInternal(n.off);}}
            public void revert(){for(Note n:notes){a.addMidiEventInternal(n.on);a.addMidiEventInternal(n.off);}}
            public String label(){return "Remove MIDI notes";}
        });
    }

    public List<Note> quantizeNotes(String assetId,List<Note> notes,int divisionsPerBeat){
        if(notes==null||notes.isEmpty())return Collections.emptyList();
        long q=project.grid.framesForDivision(divisionsPerBeat);ArrayList<Note> out=new ArrayList<>();
        for(Note n:notes){long snapped=Math.max(0,Math.round((double)n.startFrame()/q)*q);out.addAll(editNotes(assetId,Collections.singletonList(n),snapped-n.startFrame(),0));}
        return Collections.unmodifiableList(out);
    }

    private Asset midi(String id){
        Asset a=project.asset(id);
        if(a==null||a.kind!=Asset.Kind.MIDI)throw new IllegalArgumentException("MIDI asset required");
        return a;
    }
}
