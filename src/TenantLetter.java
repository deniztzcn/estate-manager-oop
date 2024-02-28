public class TenantLetter {
    private static int counter = 1;
    private int id;
    private Person person;
    private Room room;

    public TenantLetter(Person person, Room room) {
        this.person = person;
        this.room = room;
        this.id = counter++;
    }

    public Person getPerson() {
        return person;
    }

    public Room getRoom() {
        return room;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return person + " -> " + "letter[" + id +"]";
    }
}
