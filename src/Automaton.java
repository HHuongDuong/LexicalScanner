import java.util.HashMap;
import java.util.Map;

public class Automaton {
    public Map<State.STATE, LexicalScanner.Type> finalStates;

    // acceptable ending states
    public Automaton() {
        finalStates = new HashMap<>();
        finalStates.put(State.STATE.s1, LexicalScanner.Type.IntLiteral);
        finalStates.put(State.STATE.s3, LexicalScanner.Type.RealLiteral);
        finalStates.put(State.STATE.s4, LexicalScanner.Type.RealLiteral);
        finalStates.put(State.STATE.s6, LexicalScanner.Type.RealLiteral);
        finalStates.put(State.STATE.s10, LexicalScanner.Type.StrLiteral);
        finalStates.put(State.STATE.s11, LexicalScanner.Type.Identifier);
        finalStates.put(State.STATE.s12, LexicalScanner.Type.Separator);
        finalStates.put(State.STATE.s13, LexicalScanner.Type.Operator);
        finalStates.put(State.STATE.s14, LexicalScanner.Type.Operator);
        finalStates.put(State.STATE.s15, LexicalScanner.Type.Operator);
        finalStates.put(State.STATE.s16, LexicalScanner.Type.Operator);
        finalStates.put(State.STATE.s18, LexicalScanner.Type.Operator);
    }

    //transition
    public State.STATE exercuteTransition(State.STATE current, char entry) {
        switch (current) {
            case s0: {
                if (LexicalScanner.isNumber(entry)) {
                    return State.STATE.s1;
                } else if (entry == '.') {
                    return State.STATE.s7;
                } else if (entry == '"') {
                    return State.STATE.s9;
                } else if (LexicalScanner.isAlaphabet(entry)) {
                    return State.STATE.s11;
                } else if (LexicalScanner.isSeparator(entry)) {
                    return State.STATE.s12;
                } else if (entry == '+' || entry == '-' || entry == '*' || entry == '/') {
                    return State.STATE.s13;
                } else if (entry == '>' || entry == '<' || entry == '!') {
                    return State.STATE.s14;
                } else if (entry == '=') {
                    return State.STATE.s16;
                } else if (entry == '|') {
                    return State.STATE.s17;
                } else if (entry == '&') {
                    return State.STATE.s19;
                } else return State.STATE.InvalidState;
            }
            case s1: {
                if (LexicalScanner.isNumber(entry)) {
                    return State.STATE.s1;
                } else if (entry == '.') {
                    return State.STATE.s2;
                } else if (entry == 'E') {
                    return State.STATE.s4;
                } else return State.STATE.InvalidState;
            }
            case s2: {
                if (LexicalScanner.isNumber(entry)) {
                    return State.STATE.s3;
                } else return State.STATE.InvalidState;
            }
            case s3: {
                if (LexicalScanner.isNumber(entry)) {
                    return State.STATE.s3;
                } else if (entry == 'E' || entry == 'e') {
                    return State.STATE.s4;
                } else return State.STATE.InvalidState;
            }
            case s4: {
                if (LexicalScanner.isNumber(entry)) {
                    return State.STATE.s6;
                } else if (entry == '+' || entry == '-') {
                    return State.STATE.s5;
                } else return State.STATE.InvalidState;
            }
            case s5:
            case s6: {
                if (LexicalScanner.isNumber(entry)) {
                    return State.STATE.s6;
                } else return State.STATE.InvalidState;
            }
            case s7: {
                if (LexicalScanner.isNumber(entry)) {
                    return State.STATE.s8;
                } else return State.STATE.InvalidState;
            }
            case s8: {
                if (LexicalScanner.isNumber(entry)) {
                    return State.STATE.s8;
                } else if (entry == 'E') {
                    return State.STATE.s4;
                } else return State.STATE.InvalidState;
            }
            case s9: {
                if (entry == '"') {
                    return State.STATE.s10;
                } else return State.STATE.s9;
            }
            case s11: {
                if (LexicalScanner.isNumber(entry) || LexicalScanner.isAlaphabet(entry)) {
                    return State.STATE.s11;
                } else return State.STATE.InvalidState;
            }
            case s14:
            case s16: {
                if (entry == '=') {
                    return State.STATE.s15;
                } else return State.STATE.InvalidState;
            }
            case s17: {
                if (entry == '|') {
                    return State.STATE.s18;
                } else return State.STATE.InvalidState;
            }
            case s19: {
                if (entry == '&') {
                    return State.STATE.s18;
                } else return State.STATE.InvalidState;
            }
            default:
                return State.STATE.InvalidState;
        }
    }
    
}

