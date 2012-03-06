package me.lyneira.MachinaFactoryCore;

class NodeDistance implements Comparable<NodeDistance> {
    final int delay;
    private final int distance;

    NodeDistance(int delay, int distance) {
        this.delay = delay;
        this.distance = distance;
    }
    
    NodeDistance add(int delay, int distance) {
        return new NodeDistance(this.delay + delay, this.distance + distance);
    }

    @Override
    public int hashCode() {
        return delay ^ distance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeDistance other = (NodeDistance) obj;

        return delay == other.delay && distance == other.distance;
    }

    @Override
    public int compareTo(NodeDistance other) {
        int result = delay - other.delay;
        if (result == 0)
            result = distance - other.distance;
        return result;
    }
}
