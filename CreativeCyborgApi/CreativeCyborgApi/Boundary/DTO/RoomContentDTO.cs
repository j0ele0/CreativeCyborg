using CreativeCyborgApi.Entity;

namespace CreativeCyborgApi.Boundary.DTO
{
    public class RoomContentDTO
    {
        public List<ContentPart> Content { get; set; }
        public int AudioFrequenz { get; set; }

        public RoomContentDTO(List<ContentPart> content, int audioFreq) 
        {
            Content = content;
            AudioFrequenz = audioFreq;
        }
    }
}
