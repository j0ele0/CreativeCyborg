package com.example.creativcyborg.gateway.return_values;

public class TopicResponse
{
    public int httpStatusCode;
    public String recognizedText;
    public String shortTopic;

    public TopicResponse(int httpStatusCode)
    {
        this.httpStatusCode = httpStatusCode;
    }

    public TopicResponse(int httpStatusCode, String recognizedText, String shortTopic)
    {
        this.httpStatusCode = httpStatusCode;
        this.recognizedText = recognizedText;
        this.shortTopic = shortTopic;
    }
}
