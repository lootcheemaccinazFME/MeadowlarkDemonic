package com.flymaccin.meadowlarkdemonic;

import java.util.ArrayDeque;

/** Single undo/redo authority. Mutating features register commands here. */
public final class UndoHistory {
 public interface Command{void apply();void revert();String label();}
 private final ArrayDeque<Command> undo=new ArrayDeque<>(),redo=new ArrayDeque<>();
 public synchronized void execute(Command c){c.apply();undo.push(c);redo.clear();}
 public synchronized boolean canUndo(){return !undo.isEmpty();}
 public synchronized int undoDepth(){return undo.size();}
 public synchronized int redoDepth(){return redo.size();}
 public synchronized String nextUndoLabel(){return undo.isEmpty()?null:undo.peek().label();}
 public synchronized String nextRedoLabel(){return redo.isEmpty()?null:redo.peek().label();}
 public synchronized void clear(){undo.clear();redo.clear();}
 public synchronized boolean canRedo(){return !redo.isEmpty();}
 public synchronized String undo(){if(undo.isEmpty())return null;Command c=undo.pop();c.revert();redo.push(c);return c.label();}
 public synchronized String redo(){if(redo.isEmpty())return null;Command c=redo.pop();c.apply();undo.push(c);return c.label();}
}