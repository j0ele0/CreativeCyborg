using CreativeCyborgApi.Boundary.Converter;
using System.Text.Json.Serialization;

namespace CreativeCyborgApi.Boundary.DTO;

public class AudioDTO
{
    [JsonConverter(typeof(ByteArrayConverter))]
    public byte[] audioData { get; set; }
}