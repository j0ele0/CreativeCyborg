using CreativeCyborgApi.Boundary.DTO;
using CreativeCyborgApi.Entity;


namespace CreativeCyborgApi.Gateway
{
    /// <summary>
    ///     Definiert Methoden für den Zugriff auf die Raum-Daten.
    /// </summary>
    public class RoomGateway
    {
        private static int IdCounter = 1;
        private const int MAX_IDEAS_PER_ROOM = 3;
        private const int MAX_MESSAGES_PER_ROOM = 20;
        private static ICollection<Room> Rooms = new List<Room>();

        public static ICollection<Room> GetRooms()
        {
            return Rooms;
        }

        public static Room Get(long roomId)
        {
            return Rooms.FirstOrDefault(o => o.Id == roomId);
        }

        public static Room Add(CreateRoomDTO dto)
        {
            Room room = new Room(IdCounter++, dto.Name, dto.Topic);
            Rooms.Add(room);

            return room;
        }

        public static bool Remove(long id)
        {
            Room room = Rooms.FirstOrDefault(o => o.Id == id);

            if (room != null)
            {
                Rooms.Remove(room);
                return true;
            }

            return false;
        }

        public static List<ContentPart> GetCurrentRoomContent(long roomId)
        {
            Room room = Rooms.FirstOrDefault(o => o.Id == roomId);

            if (room != null)
            {
                return room.Content;
            }

            return new List<ContentPart>();
        }

        /*public static async List<ContentPart> GetCurrentRoomContentAsync(long roomId)
        {
            Room room = Rooms.FirstOrDefault(o => o.Id == roomId);

            if (room != null)
            {
                
                return room.Content;
            }

            return new List<ContentPart>();
        }*/

        public static Queue<Message> GetCurrentRoomMessages(long roomId) {
            Room room = Rooms.FirstOrDefault(o => o.Id == roomId);

            if (room != null)
            {
                return room.Messages;
            }

            return new Queue<Message>();
        }

        public static Queue<Message> AddMessageToRoom(long roomId, string role, string message) {
            Room currentRoom = Get(roomId);

            if (currentRoom == null)
            { 
                return new Queue<Message>();
            }

            currentRoom.Messages.Enqueue(new Message(role,message));
            if (currentRoom.Messages.Count >= MAX_MESSAGES_PER_ROOM) 
            {
                currentRoom.Messages.Dequeue();
            }

            return currentRoom.Messages;
        }

        public static bool ReplaceCurrentRoomContent(long roomId, List<ContentPart> newContent)
        {
            Room room = Rooms.FirstOrDefault(o => o.Id == roomId);

            if (room == null)
            {
                return false;
            }

            room.Content = newContent;

            return true;
        }

        public static bool ClearRoomContent(long roomId)
        {
            Room room = Rooms.FirstOrDefault(o => o.Id == roomId);

            if (room != null)
            {
                room.Content.Clear();
                return true;
            }

            return false;
        }

        public static int GetAudioInputFrequenzFromRoom(long roomId) 
        {
            Room room = Rooms.FirstOrDefault(o => o.Id == roomId);

            if (room != null)
            {
                return room.AudioInputFrequenz;
            }

            return -1;
        }

        public static bool SetAudioInputFrequenzFromRoom(long roomId, int audioInputFreq)
        {
            Room room = Rooms.FirstOrDefault(o => o.Id == roomId);

            if (room != null)
            {
                room.AudioInputFrequenz = audioInputFreq;
                return true;
            }

            return false;
        }
    }
}
