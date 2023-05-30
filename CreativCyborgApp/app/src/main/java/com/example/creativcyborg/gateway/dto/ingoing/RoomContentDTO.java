package com.example.creativcyborg.gateway.dto.ingoing;

import com.example.creativcyborg.entities.ContentPart;

import java.util.List;

public class RoomContentDTO
{
    public List<ContentPart> content;
    public int audioFrequenz;

    public RoomContentDTO(List<ContentPart> content, int audioFreq)
    {
        content = content;
        audioFrequenz = audioFreq;
    }
}

