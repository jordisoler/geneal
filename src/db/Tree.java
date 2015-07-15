/*
 * Copyright (C) 2015 jordi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package db;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author jordi
 */
public class Tree extends conexio{
    private Node root;
    private ArrayList<Node> nodes;

    public Tree(int rootId) {
        initialize(rootId);
    }
    
    public Tree(persona p){
        initialize(p.getId());
    }
    
    public ArrayList<Node> getNodes(){
        return this.nodes;
    }
    
    public ArrayList<ArrayList<persona>> getBranches(int p){
        ArrayList<ArrayList<persona>> result = new ArrayList<>();
        Node ntop = new Node(this.root.id);
        Node nroot = new Node(p);
        for (Node idx: this.nodes){
            if (idx.equals(nroot)){
                ArrayList<persona> branch = new ArrayList<>();
                Node nbranchloop = idx;
                while (!nbranchloop.equals(ntop)){
                    branch.add(new persona(nbranchloop.id));
                    int iidx = this.nodes.indexOf(nbranchloop);
                    nbranchloop = this.getParentNode(iidx);
                }
                result.add(branch);
            }
        }
        return result;
    }
    
    private Node getParentNode(int i){        
        return this.nodes.get(this.nodes.get(i).parent);
    }
    
    private void initialize(int rootId){
        root = new Node();
        root.id = rootId;
        nodes = new ArrayList<>();
        
        persona p = new persona(rootId);
        this.growNode(p);
    }

    private void growNode(int parent, persona p){
        persona[] fills = p.getFills();
        Node newnode = new Node(p.getId(),parent);
        this.nodes.add(newnode);
        int position = this.nodes.indexOf(newnode);
        if (fills.length!=0){
            for (persona f: fills){
                growNode(position,f);
            }
        }
    }
    
    private void growNode(persona p){
        this.growNode(-1, p);
    }
    
    public static class Node {
        private int id;
        private int parent;
        
        public Node(){
            this.id = -1;            
            this.parent = -1;
        }
        
        public Node(int id){
            this.id = id;
            this.parent = -1;
        }
        
        public Node(int id, int parent){
            this.id = id;
            this.parent = parent;
        }
        
        public boolean equals(Node n){
            return this.id == n.id;
        }
        
        
        @Override
        public String toString(){
            persona p = new persona(id);
            return p.toString();
        }
    }
}
