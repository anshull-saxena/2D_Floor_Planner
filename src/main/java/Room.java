import javax.swing.JPanel;
import java.util.ArrayList;



public class Room extends JPanel{
    // ArrayList<Room> arr = new ArrayList<Room>();
    ArrayList<Room> arrRoom = new ArrayList<Room>();
    ArrayList<Door> doors = new ArrayList<Door>();
    // l, w, roomtype, position

    public int length, width;
    public String roomtype;
    public int x, y;

    // overlaping func, doors func, walls

    public boolean overlap(Room a, Room b) {
        if ((a.x < b.x && (a.x + a.width > b.x)) || (a.x > b.x && (b.x + b.width > a.x))) {
            return true;
        } else if ((a.y < b.y && (a.y + a.length > b.y)) || (a.y > b.y && (b.y + b.length > a.y))) {
            return true;
        } else {
            return false;
        }
    }

    public Room(int x, int y, int length, int width) {
        addDoor(this);
        length = this.length;
        width = this.width;
        x = this.x;
        y = this.y;

    }

    public void addDoor(Room a) {
        ArrayList<Room> adjRooms = new ArrayList<Room>();

        for (Room room : arrRoom) {
            if (room != a && !overlap(room, a)) {
                adjRooms.add(room);
            }
        }

        if (adjRooms.size() == 1) {
            createDoor(a, adjRooms.get(0));
        } else if (adjRooms.size() > 1) {
            // creates door between Bed and wc
            // bed n kitchen, bed n drawing room, drawing room n dining space

            // random for now
            createDoor(a, adjRooms.get(0));
        }
    }

    public void createDoor(Room a, Room b) {
        if ((a.x < b.x && (a.x + a.width == b.x)) || (a.x > b.x && (b.x + b.width == a.x))) {
            doors.add(new Door(1, (int) Math.floor(a.length / 4), (int) (a.x + a.width),
                    (int) a.y + (int) Math.floor(3 * a.length / 8)));
        } else if ((a.y < b.y && (a.y + a.length == b.y)) || (a.y > b.y && (b.y + b.length == a.y))) {
            doors.add(new Door(1, (int) Math.floor(a.width / 4), (int) a.x + (int) Math.floor(3 * a.width / 8),
                    (int) a.y));
        }
    }
}