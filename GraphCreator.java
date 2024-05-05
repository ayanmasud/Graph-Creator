/*
 * Create a graph consisting of nodes and edges. Travelling Salesman included.
 * Author: Ayan Masud
 * Date: 5/5/24
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GraphCreator implements ActionListener, MouseListener { // creates the window. does the algorithm to determine whether 2 nodes are connected and finds the shortest route to a node (travelling salesman)

    JFrame frame = new JFrame();
    GraphPanel panel = new GraphPanel();
    JButton nodeB = new JButton("Node");
    JButton edgeB = new JButton("Edge");
    JTextField labelsTF = new JTextField("A");
    JTextField firstNode = new JTextField("First");
    JTextField secondNode = new JTextField("Second");
    JButton connectedB = new JButton("Test Connected");
    Container west = new Container();
    Container east = new Container();
    Container south = new Container();
    JTextField salesmanStartTF = new JTextField("A");
    JButton salesmanB = new JButton("Shortest Path");
    final int NODE_CREATE = 0;
    final int EDGE_FIRST = 1;
    final int EDGE_SECOND = 2;
    int state = NODE_CREATE;
    Node first = null;
    ArrayList<ArrayList<Node>> completed = new ArrayList<ArrayList<Node>>();

    public GraphCreator() { // creates the window visually
        frame.setSize(800,600);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        west.setLayout(new GridLayout(3,1));
        west.add(nodeB);
        nodeB.addActionListener(this);
        nodeB.setBackground(Color.green);
        west.add(edgeB);
        edgeB.setBackground(Color.lightGray);
        edgeB.addActionListener(this);
        west.add(labelsTF);
        frame.add(west, BorderLayout.WEST);
        east.setLayout(new GridLayout(3,1));
        east.add(firstNode);
        east.add(secondNode);
        east.add(connectedB);
        connectedB.addActionListener(this);
        frame.add(east, BorderLayout.EAST);
        panel.addMouseListener(this);
        south.setLayout(new GridLayout(1,2));
        south.add(salesmanStartTF);
        south.add(salesmanB);
        salesmanB.addActionListener(this);
        frame.add(south, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new GraphCreator();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(nodeB)) { // add a node to the graph
            nodeB.setBackground(Color.green);
            edgeB.setBackground(Color.lightGray);
            state = NODE_CREATE;
        }
        if(e.getSource().equals(edgeB)) { // add an edge to the graph
            nodeB.setBackground(Color.lightGray);
            edgeB.setBackground(Color.green);
            state = EDGE_FIRST;
            panel.stopHighlighting();
            frame.repaint();
        }
        if(e.getSource().equals(connectedB)) { // checks to see whether 2 nodes are somehow connected through edges
            if(panel.nodeExists(firstNode.getText()) == false) {
                JOptionPane.showMessageDialog(frame, "First node is not in your graph.");
            }
            else if(panel.nodeExists(secondNode.getText()) == false) {
                JOptionPane.showMessageDialog(frame, "Second node is not in your graph.");
            }
            else { // algorithm for checking whether the node is connected to another node through any sort of path by utilizing queues
                Queue queue = new Queue();
                ArrayList<String> connectedList = new ArrayList<String>();
                connectedList.add(panel.getNode(firstNode.getText()).getLabel());
                ArrayList<String> edges = panel.getConnectedLabels(firstNode.getText());
                for (int a = 0; a < edges.size(); a++) {
                    //check each connected node
                    queue.enqueue(edges.get(a)); // the node might get added twice but it doesn't matter
                }
                while (queue.isEmpty() == false) {
                    String currentNode = queue.dequeue();
                    if (connectedList.contains(currentNode) == false) {
                        connectedList.add(currentNode);
                    }
                    edges = panel.getConnectedLabels(currentNode);
                    for (int a = 0; a < edges.size(); a++) {
                        //check each connected node
                        if (connectedList.contains(edges.get(a)) == false) {
                            queue.enqueue(edges.get(a));
                        }
                    }
                }
                if (connectedList.contains(secondNode.getText())) {
                    JOptionPane.showMessageDialog(frame, "Connected!");
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Not Connected");
                }
            }
        }
        if (e.getSource().equals(salesmanB)) { // travelling salesman
            if (panel.getNode(salesmanStartTF.getText()) != null) {
                ArrayList<Node> path = new ArrayList<Node>(); // arraylist for the shortest path
                path.add(panel.getNode(salesmanStartTF.getText()));

                travelling(panel.getNode(salesmanStartTF.getText()), path, 0);

                if (completed.size() == 0){ // no path
                    JOptionPane.showMessageDialog(frame, "No Valid Path Found.");
                }
                else {
                    int lowestCost = Integer.MAX_VALUE; // cost of the cheapest path
                    int lowestIndex = -1; // index of the node

                    for(int i = 0; i < completed.size(); i++){
                        ArrayList<Node> currentPath = completed.get(i);

                        int cost = Integer.parseInt(currentPath.get(currentPath.size() - 1).getLabel());
                        if(cost < lowestCost){
                            lowestCost = cost;
                            lowestIndex = i;
                        }
                    }

                    String outputPath = ""; // string for outputting the order and cost
                    for (int i = 0; i < completed.get(lowestIndex).size() - 1; i++) {
                        Node n = completed.get(lowestIndex).get(i);
                        if(i == 0) {
                            outputPath += "Path: " + n.getLabel();
                        }
                        else {
                            outputPath += " -> " + n.getLabel();
                        }
                    }
                    JOptionPane.showMessageDialog(panel, outputPath += "\nLowest Cost: " + completed.get(lowestIndex).get(completed.get(lowestIndex).size() - 1).getLabel());
                }
            }
            else {
                JOptionPane.showMessageDialog(frame, "Not a valid starting node!");
            }
        }

    }

    public void travelling(Node n, ArrayList<Node> path, int total) {
        //if the number of nodes in the path is equal to the number of nodes
        //  add this path to the completed list
        //  remove the last thing in the path
        //  return
        //else
        //  for each edge
        //    see if they are connected to the current node
        //    if they are not already in the path
        //      add node to path
        //      travelling(node, path, total + edge cost);
        //remove the last thing in the path

        if (path.size() == panel.nodeList.size()){
            ArrayList<Node> otherPath = new ArrayList<Node>();
            for(Node node : path) {
                otherPath.add(node);
            }
            otherPath.add(new Node(0, 0, total + ""));// check other paths to find the shortest path
            completed.add(otherPath);
            path.remove(path.size() - 1);
            return;
        }
        else {
            for (int a = 0; a < panel.edgeList.size(); a++) {
                Edge e = panel.edgeList.get(a);
                if (e.getOtherEnd(n) != null) {
                    if (path.contains(e.getOtherEnd(n)) == false) {
                        path.add(e.getOtherEnd(n));
                        travelling(e.getOtherEnd(n), path, total + Integer.parseInt(e.getLabel()));
                    }
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(state == NODE_CREATE) {
            panel.addNode(e.getX(), e.getY(), labelsTF.getText());
        } else if (state == EDGE_FIRST) {
            Node n = panel.getNode(e.getX(), e.getY());
            if(n != null) {
                first = n;
                state = EDGE_SECOND;
                //add highlighting
                n.setHighlighted(true);
            }
        } else if (state == EDGE_SECOND) {
            Node n = panel.getNode(e.getX(), e.getY());
            if(n != null && !first.equals(n)) {
                String s = labelsTF.getText();
                boolean valid = true;
                for (int a = 0; a < s.length(); a++) { // ensures that the edge label is only numbers
                    if(Character.isDigit(s.charAt(a)) == false) {
                        valid = false;
                    }
                }
                if(valid == true) {
                    //remove highlighting
                    first.setHighlighted(false);
                    panel.addEdge(first, n, labelsTF.getText());
                    first = null;
                    state = EDGE_FIRST;
                }
                else {
                    JOptionPane.showMessageDialog(frame, "Can only have digits in edge labels.");
                }
            }
        }
        frame.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
