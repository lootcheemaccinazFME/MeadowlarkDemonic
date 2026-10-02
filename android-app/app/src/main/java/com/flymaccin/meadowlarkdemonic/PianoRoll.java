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

    private Asset midi(String id){
        Asset a=project.asset(id);
        if(a==null||a.kind!=Asset.Kind.MIDI)throw new IllegalArgumentException("MIDI asset required");
        return a;
    }
}
