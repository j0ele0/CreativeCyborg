using CreativeCyborgApi.Boundary.Converter;
using System.Text.Json.Serialization;

namespace CreativeCyborgApi.Boundary.DTO
{
    public class RoomContentRequestDTO
    {
        [JsonConverter(typeof(ByteArrayConverter))]
        public byte[] audioData { get; set; }
        public bool generateImages { get; set; }
        public float temperature { get; set; }
    }
}
