namespace CreativeCyborgApi.Boundary.DTO
{
    public class TextImageDTO
    {
        public string idea { get; set; }
        public string imageLink { get; set; }

        public TextImageDTO(string idea, string imageLink)
        {
            this.idea = idea;
            this.imageLink = imageLink;
        }
    }
}
