package src.main.java.proyecto_1_lf;

import java.util.*;

class State {
    Set<Integer> ids;  
    Map<String, State> transitions;  

    public State(Set<Integer> ids) {
        this.ids = ids;
        this.transitions = new HashMap<>();
    }

    public void addTransition(String terminal, State state) {
        transitions.put(terminal, state);
    }

    @Override
    public String toString() {
        return "State " + ids + " -> " + transitions.keySet().toString();
    }
}

public class DFA {
    Map<Integer, String> terminalMap;  
    Map<Integer, Set<Integer>> followMap; 
    List<State> states;  

    public DFA(Map<Integer, String> terminalMap, Map<Integer, Set<Integer>> followMap) {
        this.terminalMap = terminalMap;
        this.followMap = followMap;
        this.states = new ArrayList<>();
    }

    public void generateStates(Set<Integer> firstSet) {
        Queue<State> queue = new LinkedList<>();
        State initialState = new State(firstSet);
        queue.add(initialState);
        states.add(initialState);

        while (!queue.isEmpty()) {
            State currentState = queue.poll();
            Map<String, Set<Integer>> transitionSets = new HashMap<>();

            for (int id : currentState.ids) {
                String terminal = terminalMap.get(id);  
                if (terminal == null || terminal.equals("#")) {
                    continue;  
                }
                transitionSets.putIfAbsent(terminal, new HashSet<>());
                transitionSets.get(terminal).addAll(followMap.get(id)); 
            }

            for (Map.Entry<String, Set<Integer>> entry : transitionSets.entrySet()) {
                String terminal = entry.getKey();
                Set<Integer> followSet = entry.getValue();

                State nextState = findState(followSet);
                if (nextState == null) {
                    nextState = new State(followSet); 
                    states.add(nextState);
                    queue.add(nextState);
                }
                currentState.addTransition(terminal, nextState);  
            }
        }
    }

    private State findState(Set<Integer> followSet) {
        for (State state : states) {
            if (state.ids.equals(followSet)) {
                return state;
            }
        }
        return null;
    }

    public void printStates() {

        for (State state : states) {
            System.out.println(state);
            for (Map.Entry<String, State> transition : state.transitions.entrySet()) {
                System.out.println("  Terminal " + transition.getKey() + " -> " + transition.getValue().ids);
            }
        }

    }
}
