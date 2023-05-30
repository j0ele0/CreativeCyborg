using CreativeCyborgApi.Entity;
using Newtonsoft.Json;
using System.Collections;
using System.Diagnostics;
using System.Net.Http.Headers;
using System.Runtime.CompilerServices;
using System.Text.Json.Nodes;


namespace CreativeCyborgApi.Gateway.Outgoing
{
    /// <summary>
    ///     Stellt Methoden zur Verfügung, um mit den KI's von OpenAI zu interagieren.
    /// </summary>
    public static class OpenAI
    {
        private static readonly HttpClient client;
        private const string BASE_ADDRESS = "https://api.openai.com/";
        private const string OPENAI_API_KEY = "XXX";
        private const string IMAGE_SIZE = "512x512";

        public static string TEXT_SYSTEM_PROMPT = "Gib mir basierend zum genannten Thema und folgendem Text kreative Ideen in Stichpunkten. Zähle immer genau 3 Stichpunkte auf und verwende pro Stichpunkt nur maximal 2 Wörter. Beginnen jeden Stichpunkt mit einem Bindestrich: ";
        public static string TOPIC_PROMPT = "Fasse folgendes Thema in 4 Wörtern zusammen: ";

        static OpenAI()
        {
            // HttpClient konfigurieren
            client = new HttpClient();
            client.BaseAddress = new Uri(BASE_ADDRESS);
            client.DefaultRequestHeaders.Accept.Clear();
            client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
            client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", OPENAI_API_KEY);
        }

        /// <summary>
        ///     Extrahiert den gesprochenen Text aus Audiodaten mithilfe von Whisper.
        /// </summary>
        /// <param name="audioData">Die Audiodaten.</param>
        /// <returns>Gibt den Text zurück.</returns>
        public static async Task<string> ExtractTextFromAudioData(byte[] audioData)
        {
            MultipartFormDataContent formData = new MultipartFormDataContent
            {
                { new StringContent("whisper-1"), "model" },
                { new StringContent("de"), "language" },
                { new StreamContent(new MemoryStream(audioData)), "file", "mp4" }
            };

            HttpResponseMessage response = await client.PostAsync("v1/audio/transcriptions", formData);

            if (response.IsSuccessStatusCode)
            {
                string jsonContent = await response.Content.ReadAsStringAsync();
                dynamic content = JsonConvert.DeserializeObject<dynamic>(jsonContent);

                return content.text;
            }

            return "";
        }

        /// <summary>
        ///     Generiert drei kreative Ideen (Stichpunkte) aus einem Text mithilfe von ChatGPT.
        /// </summary>
        /// <param name="systemPrompt">Der Text.</param>
        /// <returns>Gibt eine Auflistung der Ideen zurück.</returns>
        public static async Task<List<string>> GenerateIdeasFromText(string systemPrompt, Queue<Message> messageArray, float temperature)
        {
            string response = await CallChatGPT(systemPrompt, messageArray, temperature);
            //Todo: die responses werden nach einigen malen kürzer und enthalten nicht mehr 3 stichpunkte
            //Console.WriteLine("ChatGPT Raw Response: "+response);
            return IdeaStringToList(response);
        }

        /// <summary>
        ///     Fasst ein Thema in kurzen Wörten zusammen.
        /// </summary>
        /// <param name="topic">Das Thema.</param>
        /// <returns>Gibt die Zusammenfassung zurück.</returns>
        public static async Task<string> GenerateTopic(string topic)
        {
            Queue<Message> userPrompts = new Queue<Message>();
            userPrompts.Enqueue(new Message("system",topic));

            return await CallChatGPT(TOPIC_PROMPT, userPrompts, 0.7f);
        }

        /// <summary>
        ///     Ruft ChatGPT mit einer Anfrage auf.
        /// </summary>
        /// <param name="systemPrompt">Der Systemprompt.</param>
        /// <param name="userPrompts">Der Userprompt.</param>
        /// <param name="temperature">Der Temperature-Float-Wert zwischen 0 und 1.</param>
        /// <returns>Gibt die Antwort von ChatGPT zurück.</returns>
        private static async Task<string> CallChatGPT(string systemPrompt, Queue<Message> messageArray, float temperature)
        {
            List<Message> messages = new List<Message>();
            messages.Add(new Message("system", systemPrompt));
            
            foreach (Message msg in messageArray)
            {
                messages.Add(msg);
            }

            var body = new
            {
                model = "gpt-3.5-turbo",
                messages = messages,
                temperature = temperature,
                presence_penalty = 0,
                frequency_penalty = 0
            };

            HttpResponseMessage response = await client.PostAsJsonAsync("v1/chat/completions", body);

            if (response.IsSuccessStatusCode)
            {
                string jsonContent = await response.Content.ReadAsStringAsync();
                if (jsonContent is not null)
                {
                    dynamic content = JsonConvert.DeserializeObject<dynamic>(jsonContent);
                    string ideaString = content.choices[0].message.content;
                    //RoomGateway.ReplaceCurrentRoomIdeas(roomId, IdeaStringToList(ideaString));
                    return ideaString;
                }

            }
            return "";
        }

        /// <summary>
        ///     Generiert ein Bild aus einem Text mithilfe von DALLE•2.
        /// </summary>
        /// <param name="text">Der Text.</param>
        /// <returns>Gibt den Link zum Bild zurück.</returns>
        public static async Task<string> GenerateImageFromText(string text)
        {
            var body = new
            {
                prompt = text,
                size = IMAGE_SIZE
            };

            HttpResponseMessage response = await client.PostAsJsonAsync("v1/images/generations", body);
            
            if (response.IsSuccessStatusCode) 
            {
                string jsonContent = await response.Content.ReadAsStringAsync();
                if (jsonContent is not null) {
                    dynamic content = JsonConvert.DeserializeObject<dynamic>(jsonContent);
                    return content.data[0].url;
                }

            }
            return "";
        }

        private static List<string> IdeaStringToList(string ideaString) 
        {
            List<string> ideaList = new List<string>();
            string[] ideaArray = ideaString.Split("\n");
            foreach (string idea in ideaArray)
            {
                if (idea.StartsWith("- "))
                {
                    ideaList.Add(idea);
                }
            }
            return ideaList;
        }

    }
}
