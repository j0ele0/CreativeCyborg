using CreativeCyborgApi.Gateway;
using CreativeCyborgApi.Entity;
using Microsoft.AspNetCore.Mvc;
using CreativeCyborgApi.Boundary.DTO;
using CreativeCyborgApi.Gateway.Outgoing;

namespace CreativeCyborgApi.Boundary
{
    /// <summary>
    ///     Stellt Methoden nach außen zur Verfügung, um die Raum-Daten abzufragen und zu manipulieren.
    /// </summary>
    [ApiController]
    [Route("room")]
    public class RoomController : ControllerBase
    {       
        /// <summary>
        ///     Gibt alle verfügbaren Räume zurück.
        /// </summary>
        [HttpGet]
        public ActionResult GetAll()
        {
            ICollection<Room> rooms = RoomGateway.GetRooms();

            if (rooms.Count == 0)
            {
                return NoContent();
            }

            return Ok(rooms);
        }

        /// <summary>
        ///     Erstellt einen neuen Raum.
        /// </summary>
        /// <param name="dto">Die Daten des Raums.</param>
        [HttpPost]
        public ActionResult Add([FromBody] CreateRoomDTO dto)
        {
            dto.Topic = OpenAI.GenerateTopic(dto.Topic).Result;
            
            Room room = RoomGateway.Add(dto);

            return Ok(room);
        }

        /// <summary>
        ///     Setzt das Thema eines Raumes und gibt eine verkürzte Zusammenfassung von diesem zurück.
        /// </summary>
        /// <param name="roomId">Die ID des Raums.</param>
        /// <param name="dto">Die Audiodaten.</param>
        [HttpPatch("{roomId}/topic")]
        public ActionResult SetTopic(long roomId, [FromBody] AudioDTO dto)
        {
            Room room = RoomGateway.Get(roomId);

            if (room == null)
            {
                return NotFound();
            }

            string text = OpenAI.ExtractTextFromAudioData(dto.audioData).Result;
            string topic = OpenAI.GenerateTopic(text).Result;

            // TODO: Topic von Raum setzen
            room.Topic = topic;

            var response = new
            {
                text,
                topic
            };

            return Ok(response);
        }

        /// <summary>
        ///     Löscht einen Raum.
        /// </summary>
        /// <param name="id">Die ID des Raums.</param>
        [HttpDelete("{id}")]
        public ActionResult Remove(long id)
        {
            bool deleted = RoomGateway.Remove(id);

            if (deleted)
            {
                return Ok();
            }

            return NotFound();
        }

    }
}
