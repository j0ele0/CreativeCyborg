using System.ComponentModel.DataAnnotations;
using System.Runtime.Serialization;

namespace CreativeCyborgApi.Entity
{
    public class Message
    {
        
        public string Role { get; set; }
        public string Content { get; set; }

        public Message(string role, String message) {
            this.Role = role;
            this.Content = message;
        }

    }
}
