import java.util.*;

class DonationGraph {
    private static final int max = 100;
    String[] nodes;
    int[][] edges;
    int index;

    public DonationGraph() {
        nodes = new String[max];
        edges = new int[max][max];
        index = 0;
    }

    public void addnode(String name) {
        nodes[index++] = name;
    }

    public void donate(String donor, String org, int days) {
        int donorindex = findindex(donor);
        int orgindex = findindex(org);

        if (donorindex != -1 && orgindex != -1) {
            edges[donorindex][orgindex] = days;
        } else {
            System.out.println("Donor or organization not found.");
        }
    }

    public void addcapacity(String from, String to, int capacity) {
        int fromindex = findindex(from);
        int toindex = findindex(to);

        if (fromindex != -1 && toindex != -1) {
            edges[fromindex][toindex] = capacity;
        } else {
            System.out.println("Node not found.");
        }
    }

    public void printgraph() {
        System.out.println("Graph:");
        for (int i = 0; i < index; i++) {
            System.out.print(nodes[i] + ": ");

            boolean hasEdges = false;
            for (int j = 0; j < index; j++) {
                if (edges[i][j] != 0) {
                    System.out.print(nodes[j] + " : " + edges[i][j] + " days, ");
                    hasEdges = true;
                }
            }

            if (!hasEdges) {
                System.out.println("No connections");
            } else {
                System.out.println();
            }
        }
    }

    public int shortestdays(String start, String end) {
        int starti = findindex(start);
        int endi = findindex(end);

        if (starti == -1 || endi == -1) {
            System.out.println("Start or end node not found.");
            return -1;
        }

        int[] distance = new int[max];
        int[] parent = new int[max];
        boolean[] visited = new boolean[max];

        for (int i = 0; i < max; i++) {
            distance[i] = Integer.MAX_VALUE;
            parent[i] = -1;
            visited[i] = false;
        }

        distance[starti] = 0;

        PriorityQueue<Integer> queue = new PriorityQueue<>((a, b) -> Integer.compare(distance[a], distance[b]));
        queue.add(starti);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            visited[current] = true;

            for (int i = 0; i < max; i++) {
                if (!visited[i] && edges[current][i] != 0) {
                    int newDistance = distance[current] + edges[current][i];
                    if (newDistance < distance[i]) {
                        distance[i] = newDistance;
                        parent[i] = current;
                        queue.add(i);
                    }
                }
            }
        }

        printpath(parent, starti, endi);
        return distance[endi] == Integer.MAX_VALUE ? -1 : distance[endi];
    }

    private void printpath(int[] parent, int start, int end) {
        Stack<Integer> stack = new Stack<>();
        int current = end;

        while (current != -1) {
            stack.push(current);
            current = parent[current];
        }

        System.out.print("Shortest path: ");
        while (!stack.isEmpty()) {
            int nodeindex = stack.pop();
            System.out.print(nodes[nodeindex]);
            if (!stack.isEmpty()) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    public boolean detectcycle(String target) {
        boolean[] visited = new boolean[max];
        int targetindex = findindex(target);

        if (targetindex == -1) {
            System.out.println("Node not found.");
            return false;
        }

        for (int i = 0; i < index; i++) {
            if (!visited[i]) {
                if (detectcycle2(i, visited, -1, targetindex)) {
                    System.out.println("Fraud case detected! There is a cycle involving " + target);
                    return true;
                }
            }
        }

        System.out.println("No fraud case detected involving " + target);
        return false;
    }

    private boolean detectcycle2(int node, boolean[] visited, int parent, int targetIndex) {
        Queue<Integer> queue = new LinkedList<>();
        queue.add(node);
        visited[node] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int i = 0; i < index; i++) {
                if (edges[current][i] != 0) {
                    if (!visited[i]) {
                        queue.add(i);
                        visited[i] = true;
                    } else if (i != parent && i == targetIndex) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private int findindex(String nodename) {
        for (int i = 0; i < index; i++) {
            if (nodes[i].equals(nodename)) {
                return i;
            }
        }
        return -1;
    }

    public int maxFlow(String source, String sink) {
        int sourceIndex = findindex(source);
        int sinkIndex = findindex(sink);

        if (sourceIndex == -1 || sinkIndex == -1) {
            System.out.println("Source or sink node not found.");
            return -1;
        }

        int[][] residualGraph = new int[max][max];
        for (int i = 0; i < max; i++) {
            for (int j = 0; j < max; j++) {
                residualGraph[i][j] = edges[i][j];
            }
        }

        int maxFlow = 0;
        int[] parent = new int[max];

        while (bfs(residualGraph, sourceIndex, sinkIndex, parent)) {
            int pathFlow = Integer.MAX_VALUE;

            for (int v = sinkIndex; v != sourceIndex; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residualGraph[u][v]);
            }

            for (int v = sinkIndex; v != sourceIndex; v = parent[v]) {
                int u = parent[v];
                residualGraph[u][v] -= pathFlow;
                residualGraph[v][u] += pathFlow;
            }

            maxFlow += pathFlow;
        }

        return maxFlow;
    }

    private boolean bfs(int[][] graph, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[max];
        Queue<Integer> queue = new LinkedList<>();

        Arrays.fill(parent, -1);

        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int v = 0; v < max; v++) {
                if (!visited[v] && graph[u][v] > 0) {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;

                    if (v == sink) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

public class DonationTracker {
    public static void main(String[] args) {
        DonationGraph donationGraph = new DonationGraph();
        Scanner scanner = new Scanner(System.in);

        donationGraph.addnode("sayed");
        donationGraph.addnode("anik");
        donationGraph.addnode("bably");
        donationGraph.addnode("israt");
        donationGraph.addnode("nabila");
        donationGraph.addnode("abir");
        donationGraph.addnode("jiyon");
        donationGraph.addnode("punni");
        donationGraph.addnode("tanveer");

        donationGraph.donate("sayed", "anik", 5);
        donationGraph.donate("tanveer", "punni", 5);
        donationGraph.donate("anik", "israt", 3);
        donationGraph.donate("bably", "jiyon", 3);
        donationGraph.donate("jiyon", "israt", 4);
        donationGraph.donate("israt", "sayed", 6);
        donationGraph.donate("anik", "punni", 4);

        while (true) {
            System.out.println("Options:");
            System.out.println("1. Add donor/organization");
            System.out.println("2. Donate");
            System.out.println("3. Print graph");
            System.out.println("4. Find shortest days");
            System.out.println("5. Check fraud case");
            System.out.println("6. Add capacity");
            System.out.println("7. Find maximum flow");
            System.out.println("8. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter donor/organization name: ");
                    String name = scanner.nextLine();
                    donationGraph.addnode(name);
                    break;
                case 2:
                    System.out.print("Enter donor name: ");
                    String donor = scanner.nextLine();
                    System.out.print("Enter organization name: ");
                    String organization = scanner.nextLine();
                    System.out.print("Enter days of transfer: ");
                    int days = scanner.nextInt();
                    donationGraph.donate(donor, organization, days);
                    break;
                case 3:
                    donationGraph.printgraph();
                    break;
                case 4:
                    System.out.print("Enter source: ");
                    String startNode = scanner.nextLine();
                    System.out.print("Enter end node: ");
                    String endNode = scanner.nextLine();
                    int shortestDays = donationGraph.shortestdays(startNode, endNode);

                    if (shortestDays != -1) {
                        System.out.println("Shortest days from " + startNode + " to " + endNode + ": " + shortestDays);
                    } else {
                        System.out.println("Invalid nodes. Please try again.");
                    }
                    break;
                case 5:
                    System.out.print("Enter node to check for fraud case: ");
                    String targetNode = scanner.nextLine();
                    donationGraph.detectcycle(targetNode);
                    break;
                case 6:
                    System.out.print("Enter source node: ");
                    String fromNode = scanner.nextLine();
                    System.out.print("Enter sink node: ");
                    String toNode = scanner.nextLine();
                    System.out.print("Enter capacity: ");
                    int capacity = scanner.nextInt();
                    donationGraph.addcapacity(fromNode, toNode, capacity);
                    break;
                case 7:
                    System.out.print("Enter source node: ");
                    String sourceNode = scanner.nextLine();
                    System.out.print("Enter sink node: ");
                    String sinkNode = scanner.nextLine();
                    int maxFlow = donationGraph.maxFlow(sourceNode, sinkNode);

                    if (maxFlow != -1) {
                        System.out.println("Maximum flow from " + sourceNode + " to " + sinkNode + ": " + maxFlow);
                    } else {
                        System.out.println("Invalid nodes. Please try again.");
                    }
                    break;
                case 8:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
