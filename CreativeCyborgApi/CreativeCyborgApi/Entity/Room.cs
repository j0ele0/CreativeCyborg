using CreativeCyborgApi.Gateway.Outgoing;
using System.Text.Json.Serialization;

namespace CreativeCyborgApi.Entity
{
    /// <summary>
    ///     Definiert den Aufbau eines Raums.
    /// </summary>
    public class Room
    {

        /// <summary>
        ///     Die ID des Raums.
        /// </summary>
        public long Id { get; set; }

        /// <summary>
        ///     Der Name des Raums.
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        ///     Das Thema des Raums.
        /// </summary>
        public string Topic { get; set; }

        /// <summary>
        ///     Die Inhalte des Raums.
        /// </summary>
        [JsonIgnore]
        public List<ContentPart> Content;

        /// <summary>
        ///     Der Nachrichtenverlauf vom Nutzer zu ChatGPT.
        /// </summary>
        [JsonIgnore]
        public Queue<Message> Messages { get; set; }

        public int AudioInputFrequenz { get; set; }

        public Room(long id, string name, string topic = "", int audioInputFreq = 10)
        {
            Id = id;
            Name = name;
            Topic = topic;
            Content = new List<ContentPart>();
            Messages = new Queue<Message>();
            AudioInputFrequenz = audioInputFreq;
        }

    }
}
