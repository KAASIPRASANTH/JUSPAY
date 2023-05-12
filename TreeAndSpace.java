import java.util.*;

class TreeAndSpace {
    static Map<String, Node> tree;

    static class Node {
        int id, desCount, ancCount;
        List<Node> childrens;
        boolean isLocked;
        Node parent;

        Node() {
            this.isLocked = false;
            this.parent = null;
            this.id = -1;
            this.desCount = 0;
            this.ancCount = 0;
            this.childrens = new ArrayList<>();
        }
    }

    public static void BuildTree(String[] nodes, int N, int K) {
        Node root = new Node();
        tree = new HashMap<>();

        tree.put(nodes[0], root);
        Queue<Node> q = new LinkedList<>();
        q.add(root);
        int pos = 1;

        while (q.size() > 0) {
            Node curr = q.poll();
            for (int i = 0; i < K && pos < N; i++) {
                Node nn = new Node();
                tree.put(nodes[pos++], nn);
                nn.parent = curr;
                curr.childrens.add(nn);
                q.add(nn);
            }
        }
    }

    public static void changeInParent(Node curr, int value) {
        while (curr != null) {
            curr.desCount += value;
            curr = curr.parent;
        }
    }

    public static void changeInChildrens(Node curr, int value) {
        for (Node children : curr.childrens) {
            children.ancCount += value;
            changeInChildrens(children, value);
        }
    }

    public static boolean lock(Node curr, int userId) {
        // base conditions
        if (curr.isLocked) {
            return false;
        }
        if (curr.desCount > 0 || curr.ancCount > 0) {
            return false;
        }

        // make changes
        changeInParent(curr.parent, 1);
        changeInChildrens(curr, 1);
        curr.isLocked = true;
        curr.id = userId;
        return true;
    }

    public static boolean unlock(Node curr, int userId) {
        // base condition
        if (curr.isLocked == false|| curr.id != userId) {
            return false;
        }
        // make changes
        changeInParent(curr.parent, -1);
        changeInChildrens(curr, -1);
        curr.isLocked = false;
        curr.id = -1;
        return true;
    }

    public static boolean getAllChildrens(Node curr,int userId,List<Node> lockedChildrens) {
        if(curr.isLocked){
            if(userId != curr.id){
                return false;
            }else{
                lockedChildrens.add(curr);
            }
        }
        
        if(curr.desCount == 0){
            return true;
        }
        
        // a.add(node);
        for(Node adj : curr.childrens){
            boolean ans = getAllChildrens(adj,userId,lockedChildrens);
            if(ans == false) return false;
        }
        
        return true;
    }

    public static boolean upgrade(Node curr, int userId) {
        // base condition
        if (curr.isLocked || curr.desCount == 0 || curr.ancCount > 0) {
            return false;
        }
        List<Node> lockedChildrens = new ArrayList<>();
        boolean canUpgrade = getAllChildrens(curr, userId,lockedChildrens);
        if (canUpgrade == false) {
            return false;
        }
        // make changes
        for (Node node : lockedChildrens) {
            unlock(node, userId);
        }

        //locking the current node
        lock(curr,userId);
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        // Number of nodes
        int N = sc.nextInt();
        // Number of childrens per node
        int K = sc.nextInt();
        // Number of queries
        int Q = sc.nextInt();
        String[] nodes = new String[N];
        for (int i = 0; i < N; i++) {
            nodes[i] = sc.next();
        }

        // Building an Tree
        BuildTree(nodes, N, K);
        //System.out.println(tree);
        // Executing Queries
        for (int i = 0; i < Q; i++) {
            int type = sc.nextInt();
            String node = sc.next();
            int userId = sc.nextInt();

            if (type == 1) {
                System.out.println(lock(tree.get(node), userId));
            } else if (type == 2) {
                System.out.println(unlock(tree.get(node), userId));
            } else if (type == 3) {
                System.out.println(upgrade(tree.get(node), userId));
            }
        }
        sc.close();
    }
}