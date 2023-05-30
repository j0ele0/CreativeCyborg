using CreativeCyborgApi.Boundary.DTO;
using CreativeCyborgApi.Entity;
using CreativeCyborgApi.Gateway;
using CreativeCyborgApi.Gateway.Outgoing;
using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Diagnostics;


namespace CreativeCyborgApi.Boundary
{
    /// <summary>
    ///     Stellt Methoden nach außen zur Verfügung, um die Inhalte von Räumen abzufragen und zu manipulieren.
    /// </summary>
    [ApiController]
    [Route("room/{roomId}/content")]
    public class RoomContentController : ControllerBase
    {


        /// <summary>
        ///     Gibt die aktuellen Inhalte eines Raums zurück.        
        /// </summary>
        /// <param name="roomId">Die ID des Raums.</param>
        [HttpGet]
        public ActionResult GetCurrentRoomContent(long roomId)
        {
            List<ContentPart> content = RoomGateway.GetCurrentRoomContent(roomId);
            int audioFreq = RoomGateway.GetAudioInputFrequenzFromRoom(roomId);

            if (content.Count != 0 && audioFreq != -1)
            {
                return Ok(new RoomContentDTO(content, audioFreq));
            }

            return NoContent();
        }

        /// <summary>
        ///     Gibt die aktuellen Inhalte eines Raums zurück.        
        /// </summary>
        /// <param name="roomId">Die ID des Raums.</param>
        /*[HttpGet("async")]
        public Task<ActionResult> GetCurrentRoomContentAsync(long roomId)
        {
            int audioFreq = RoomGateway.GetAudioInputFrequenzFromRoom(roomId);
            List<ContentPart> content = RoomGateway.GetCurrentRoomContent(roomId);

            if (content.Count != 0 && audioFreq != -1)
            {
                return Ok(new RoomContentDTO(content, audioFreq));
            }

            return NotFound();
        }*/

        [HttpDelete]
        public ActionResult ClearRoomContent(long roomId)
        {
            bool roomFound = RoomGateway.ClearRoomContent(roomId);

            if (roomFound)
            {
                return Ok();
            }

            return NotFound();
        }

    }
}
