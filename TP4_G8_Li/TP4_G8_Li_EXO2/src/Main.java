import javax.swing.tree.TreeNode;
import java.util.*;
import java.util.Map;

public class Main {

    public static class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val){
            this.val = val;}
    }
    public static void main(String[]args){
        TreeNode graphenode1 = new TreeNode(13);
        TreeNode graphenode2 = new TreeNode(8);
        TreeNode graphenode3 = new TreeNode (17);
        TreeNode graphenode4 = new TreeNode(1);
        TreeNode graphenode5 = new TreeNode(11);
        TreeNode graphenode6 = new TreeNode(15);
        TreeNode graphenode7 = new TreeNode(25);
        TreeNode graphenode8 = new TreeNode(22);
        TreeNode graphenode9 = new TreeNode(27);
        TreeNode graphenode10 = new TreeNode(6);
        graphenode1.left = graphenode2;
        graphenode1.right = graphenode3;

        graphenode2.left = graphenode4;
        graphenode2.right = graphenode5;

        graphenode3.left = graphenode6;
        graphenode3.right = graphenode7;

        graphenode4.left = graphenode10;

        graphenode7.left = graphenode8;
        graphenode7.right = graphenode9;

        breadthFirstSearch(treemap(),13);
        depthFirstSearch(treemap(),13);
        BFS(graphenode1);
        DFS(graphenode1);
    }

    public static TreeMap treemap(){
        TreeMap<Integer ,String> graphe = new TreeMap<Integer, String>();
        graphe.put(13,null);
        graphe.put(8,null);
        graphe.put(17,null);
        graphe.put(1,null);
        graphe.put(11,null);
        graphe.put(15,null);
        graphe.put(25,null);
        graphe.put(22,null);
        graphe.put(27,null);
        graphe.put(6,null);
        return graphe;
    }

    public static void breadthFirstSearch(TreeMap graphe, int depart){
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        LinkedHashSet discovered = new LinkedHashSet();
        LinkedList path = new LinkedList();
        queue.addFirst(depart);
        while (!queue.isEmpty()){
            Integer node = queue.poll();
            if(!discovered.contains(node)){
                discovered.add(node);
                path.add(node);
                if(!discovered.contains(( graphe.headMap(node)))){
                    discovered.add(graphe.headMap(node));
                    path.add(graphe.headMap(node));
                }
                if(!discovered.contains(( graphe.tailMap(node)))){
                    discovered.add(graphe.tailMap(node));
                    path.add(graphe.tailMap(node));}

            }
        }
        System.out.println(path);
    }
    public static void depthFirstSearch(TreeMap graphe, int depart){
        Deque<Integer> pile = new ArrayDeque<>();
        LinkedHashSet discovered = new LinkedHashSet();
        LinkedList path = new LinkedList();
        pile.addFirst(depart);
        while (!pile.isEmpty()){
            Integer node = pile.poll();
            if(!discovered.contains(node)){
                discovered.add(node);
                path.add(node);
                if(!discovered.contains(( graphe.headMap(node)))){
                    discovered.add(graphe.headMap(node));
                    path.add(graphe.headMap(node));
                }
                if(!discovered.contains(( graphe.tailMap(node)))){
                    discovered.add(graphe.tailMap(node));
                    path.add(graphe.tailMap(node));}

            }
        }
        System.out.println(path);
    }
    public static void BFS(TreeNode depart){
        ArrayDeque<TreeNode> queue = new ArrayDeque<>();
        LinkedHashSet discovered = new LinkedHashSet();
        LinkedList path = new LinkedList();
        queue.addFirst(depart);
        while (!queue.isEmpty()){
            TreeNode node = queue.poll();
            if(!discovered.contains(node)){
                discovered.add(node);
                path.add(node.val);
                if (node.left != null){
                    discovered.add(node.left.val);
                    path.add(node.left.val);
                    if (node.left.left != null){
                        discovered.add(node.left.left.val);
                        path.add(node.left.left.val);
                        if (node.left.left.left != null){
                            discovered.add(node.left.left.left.val);
                            path.add(node.left.left.left.val);}
                        if (node.left.left.right != null){
                            discovered.add(node.left.left.right.val);
                            path.add(node.left.left.right.val);}}
                    if (node.left.right != null){
                        discovered.add(node.left.right.val);
                        path.add(node.left.right.val);}
                    if (node.left.right.right != null){
                        discovered.add(node.left.right.right.val);
                        path.add(node.left.right.right.val);}
                    if (node.left.right.left != null){
                        discovered.add(node.left.right.left.val);
                        path.add(node.left.right.left.val);}
                }


            }if (node.right != null){
                discovered.add(node.right.val);
                path.add(node.right.val);
                if (node.right.right != null){
                    discovered.add(node.right.right.val);
                    path.add(node.right.right.val);
                    if (node.right.right.left != null){
                        discovered.add(node.right.right.left.val);
                        path.add(node.right.right.left.val);}
                    if (node.right.right.right != null){
                        discovered.add(node.right.right.right.val);
                        path.add(node.right.right.right.val);}}

                if (node.right.left != null){
                    discovered.add(node.right.left.val);
                    path.add(node.right.left.val);
                    if (node.right.left.right != null){
                        discovered.add(node.right.left.right.val);
                        path.add(node.right.left.right.val);}
                    if (node.right.left.left != null){
                        discovered.add(node.right.left.left.val);
                        path.add(node.right.left.left.val);}
                }
        }
        System.out.println(path);
    }}
    
    public static void DFS(TreeNode depart){
        Deque<TreeNode> pile = new ArrayDeque<>();
        LinkedHashSet discovered = new LinkedHashSet();
        LinkedList path = new LinkedList();
        pile.addFirst(depart);
        while (!pile.isEmpty()){
            TreeNode node = pile.poll();
            if(!discovered.contains(node)){
                discovered.add(node);
                path.add(node.val);
                if (node.left != null){
                    discovered.add(node.left.val);
                    path.add(node.left.val);
                    if (node.left.left != null){
                        discovered.add(node.left.left.val);
                        path.add(node.left.left.val);
                        if (node.left.left.left != null){
                            discovered.add(node.left.left.left.val);
                            path.add(node.left.left.left.val);}
                        if (node.left.left.right != null){
                            discovered.add(node.left.left.right.val);
                            path.add(node.left.left.right.val);}}
                    if (node.left.right != null){
                        discovered.add(node.left.right.val);
                        path.add(node.left.right.val);}
                    if (node.left.right.right != null){
                        discovered.add(node.left.right.right.val);
                        path.add(node.left.right.right.val);}
                    if (node.left.right.left != null){
                        discovered.add(node.left.right.left.val);
                        path.add(node.left.right.left.val);}
                }


                }if (node.right != null){
                    discovered.add(node.right.val);
                    path.add(node.right.val);
                    if (node.right.right != null){
                        discovered.add(node.right.right.val);
                        path.add(node.right.right.val);
                        if (node.right.right.left != null){
                            discovered.add(node.right.right.left.val);
                            path.add(node.right.right.left.val);}
                        if (node.right.right.right != null){
                            discovered.add(node.right.right.right.val);
                            path.add(node.right.right.right.val);}}

                    if (node.right.left != null){
                        discovered.add(node.right.left.val);
                        path.add(node.right.left.val);
                        if (node.right.left.right != null){
                            discovered.add(node.right.left.right.val);
                            path.add(node.right.left.right.val);}
                        if (node.right.left.left != null){
                            discovered.add(node.right.left.left.val);
                            path.add(node.right.left.left.val);}
                    }}
                }
                System.out.println(path);
            }

        }

