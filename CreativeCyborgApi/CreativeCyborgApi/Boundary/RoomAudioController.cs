using CreativeCyborgApi.Boundary.DTO;
using CreativeCyborgApi.Entity;
using CreativeCyborgApi.Gateway.Outgoing;
using CreativeCyborgApi.Gateway;
using Microsoft.AspNetCore.Mvc;

namespace CreativeCyborgApi.Boundary
{
    [ApiController]
    [Route("room/{roomId}/audio")]
    public class RoomAudioController : ControllerBase
    {
        /// <summary>
        ///     Verarbeitet die empfangenen Audiodaten. 
        /// </summary>
        /// <param name="dto">Die Daten.</param>
        [HttpPost]
        public async Task<ActionResult> ProcessAudioData(long roomId, [FromBody] RoomContentRequestDTO request)
        {
            Room room = RoomGateway.Get(roomId);

            if (room == null)
            {
                return NotFound();
            }

            // Audio in Text umwandeln
            string text = await OpenAI.ExtractTextFromAudioData(request.audioData);
            RoomGateway.AddMessageToRoom(roomId, "user", text);

            // Systempromopt erzeugen
            string systemPrompt = $"Es geht um folgendes Thema: {room.Topic}. {OpenAI.TEXT_SYSTEM_PROMPT}";

            // ChatGPT zur Ideengenerierung aufrufen
            var newIdeas = await OpenAI.GenerateIdeasFromText(systemPrompt, room.Messages, roomId);


            List<ContentPart> newContent = new List<ContentPart>();
            // ggf. Bilder generieren
            if (request.generateImages)
            {
                List<Thread> threads = new List<Thread>();
                foreach (var newIdea in newIdeas)
                {
                    Thread thread = new Thread(() =>
                    {
                        var newImage = OpenAI.GenerateImageFromText(newIdea).Result;
                        newContent.Add(new ContentPart()
                        {
                            Idea = newIdea,
                            ImageLink = newImage
                        });
                    });
                    thread.Start();
                    threads.Add(thread);
                }

                foreach (Thread t in threads)
                {
                    t.Join();
                }

            }
            else
            {
                foreach (var newIdea in newIdeas)
                {
                    newContent.Add(new ContentPart()
                    {
                        Idea = newIdea,
                        ImageLink = ""
                    });
                }
            }

            RoomGateway.ReplaceCurrentRoomContent(roomId, newContent);

            //List<ContentPart> content = RoomGateway.GetCurrentRoomContent(roomId);
            //int audioFreq = RoomGateway.GetAudioInputFrequenzFromRoom(roomId);

            return Ok();
        }

        /// <summary>
        ///     Debug Endpoint der keine Audiodaten empfängt sonderen direkt den string-text.
        /// </summary>
        [HttpPost("debug")]
        public async Task<ActionResult> Debug(long roomId, string text, bool generateImages, float temperature)
        {
            Room room = RoomGateway.Get(roomId);

            if (room == null)
            {
                return NotFound();
            }

            RoomGateway.AddMessageToRoom(roomId, "user", text);

            // Systempromopt erzeugen
            string systemPrompt = $"Es geht um folgendes Thema: {room.Topic}. {OpenAI.TEXT_SYSTEM_PROMPT}";

            // ChatGPT zur Ideengenerierung aufrufen
            List<string> newIdeas = await OpenAI.GenerateIdeasFromText(systemPrompt, room.Messages, temperature);

            foreach (string newIdea in newIdeas)
            {
                RoomGateway.AddMessageToRoom(roomId, "assistant", newIdea);
            }

            List<ContentPart> newContent = new List<ContentPart>();
            // ggf. Bilder generieren
            if (generateImages)
            {
                List<Thread> threads = new List<Thread>();
                foreach (var newIdea in newIdeas)
                {
                    Thread thread = new Thread(() =>
                    {
                        var newImage = OpenAI.GenerateImageFromText(newIdea).Result;
                        newContent.Add(new ContentPart()
                        {
                            Idea = newIdea,
                            ImageLink = newImage
                        });
                        //RoomGateway.AddTextImageToRoom(roomId, new TextImageDTO(newIdea, newImage));
                    });
                    thread.Start();
                    threads.Add(thread);
                }

                foreach (Thread t in threads)
                {
                    t.Join();
                }

            }
            else
            {
                foreach (var newIdea in newIdeas)
                {
                    newContent.Add(new ContentPart()
                    {
                        Idea = newIdea,
                        ImageLink = ""
                    });
                }
                //RoomGateway.ReplaceCurrentRoomIdeas(roomId, newIdeas);
            }

            RoomGateway.ReplaceCurrentRoomContent(roomId, newContent);

            List<ContentPart> content = RoomGateway.GetCurrentRoomContent(roomId);
            int audioFreq = RoomGateway.GetAudioInputFrequenzFromRoom(roomId);

            return Ok(new RoomContentDTO(content, audioFreq));
        }

        [HttpGet("inputFreq")]
        public ActionResult GetAudioInputFrequenz(long roomId)
        {
            int audioInputFreq = RoomGateway.GetAudioInputFrequenzFromRoom(roomId);

            if (audioInputFreq != -1)
            {
                return Ok(audioInputFreq);
            }
            return NotFound();
        }

        [HttpPatch("inputFreq/{audioInputFreq}")]
        public ActionResult SetAudioInputFrequenz(long roomId, int audioInputFreq)
        {
            if (audioInputFreq < 10)
            {
                return BadRequest();
            }
            
            bool successful = RoomGateway.SetAudioInputFrequenzFromRoom(roomId, audioInputFreq);

            if (successful)
            {
                return Ok(audioInputFreq);
            }
            return NotFound();
        }


    }
}
