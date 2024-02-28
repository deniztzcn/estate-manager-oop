public abstract class Insertable implements Comparable<Insertable> {
    private int volume;
    private String name;
    private int id;
    private static int counter = 1;


    public Insertable(int volume, String name) {
        this.volume = volume;
        this.name = name;
        this.id = counter++;
    }

    public int getVolume() {
        return volume;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "["+ id+"]";
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Insertable o) {
        if((o.getVolume() - this.volume) > 0){
            return 1;
        } else if (o.getVolume() == this.volume){
            return this.name.compareTo(o.getName());
        } else {
            return -1;
        }
    }
}
