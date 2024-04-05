import java.util.concurrent.atomic.AtomicInteger;

class Electorate implements Runnable {
    private static final AtomicInteger voteCountA = new AtomicInteger(0);
    private static final AtomicInteger voteCountB = new AtomicInteger(0);
    private static final Object lock = new Object();

    private final int electorateId;
    private final char vote;

    public Electorate(int electorateId, char vote) {
        this.electorateId = electorateId;
        this.vote = vote;
    }

    @Override
    public void run() {
        castVote();
        displayVote();
    }

    private void castVote() {
        synchronized (lock) {
            if (vote == 'A') {
                voteCountA.incrementAndGet();
            } else if (vote == 'B') {
                voteCountB.incrementAndGet();
            }
        }
    }

    private void displayVote() {
        System.out.println("Electorate " + electorateId + " casts vote for candidate " + vote);
    }

    public static void main(String[] args) {
        Thread[] electorates = new Thread[5];
        for (int i = 0; i < 5; i++) {
            char vote = (i % 2 == 0) ? 'A' : 'B';
            electorates[i] = new Thread(new Electorate(i, vote));
            electorates[i].start();
        }

        // Wait for all electorates to finish voting
        for (Thread electorate : electorates) {
            try {
                electorate.join();
            } catch (InterruptedException e) {
                System.err.println("Error while waiting for thread: " + e.getMessage());
            }
        }

        char winner = determineWinner();
        System.out.println("Winner: Candidate " + winner);
    }

    private static char determineWinner() {
        int votesA = voteCountA.get();
        int votesB = voteCountB.get();

        if (votesA > votesB) {
            return 'A';
        } else if (votesB > votesA) {
            return 'B';
        } else {
            return 'T'; // Tie
        }
    }
}
