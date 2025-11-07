package edu.citadel.main;

/**
 * Contains prompt templates for AI interactions with Google Gemini.
 * These prompts are used to guide the AI in generating structured responses
 * for location suggestions and POI recommendations.
 */
public class Prompts {

    /**
     * Prompt template for generating location suggestions based on user queries.
     * Expected response format: JSON with array of suggestions containing name, type, address, and reason.
     */
    public static final String SUGGESTION_PROMPT =
        "You are a helpful travel and location assistant. " +
        "The user is asking: \"%s\"\n\n" +
        "Please provide 3-5 location suggestions that would be relevant to their request. " +
        "For each suggestion, provide:\n" +
        "- name: The name of the place\n" +
        "- type: The type of location (e.g., 'Coffee Shop', 'Restaurant', 'Park')\n" +
        "- address: A plausible address or general location\n" +
        "- reason: A brief explanation (1-2 sentences) of why this suggestion is relevant\n\n" +
        "Return your response as a JSON object with this exact structure:\n" +
        "{\n" +
        "  \"suggestions\": [\n" +
        "    {\n" +
        "      \"name\": \"Example Place\",\n" +
        "      \"type\": \"Coffee Shop\",\n" +
        "      \"address\": \"123 Main St, City, State\",\n" +
        "      \"reason\": \"This place meets your needs because...\"\n" +
        "    }\n" +
        "  ]\n" +
        "}\n\n" +
        "IMPORTANT: Return ONLY the JSON, no additional text or markdown formatting.";

    /**
     * Prompt template for generating POI recommendations along a route.
     * Expected response format: JSON with array of suggestions containing name, type, address, and reason.
     */
    public static final String POI_ROUTE_PROMPT =
        "You are a helpful travel assistant specialized in finding points of interest along travel routes.\n\n" +
        "The user is traveling from \"%s\" to \"%s\".\n\n" +
        "Please suggest 3-5 interesting or useful points of interest (POIs) along this route. " +
        "Consider places that would be convenient stops, including:\n" +
        "- Rest stops and service stations\n" +
        "- Restaurants and cafes\n" +
        "- Scenic viewpoints or landmarks\n" +
        "- Tourist attractions\n\n" +
        "For each POI, provide:\n" +
        "- name: The name of the place\n" +
        "- type: The type of location (e.g., 'Gas Station', 'Restaurant', 'Scenic Overlook')\n" +
        "- address: A specific or approximate address along the route\n" +
        "- reason: A brief explanation (1-2 sentences) of why this is a good stop on this particular route\n\n" +
        "Return your response as a JSON object with this exact structure:\n" +
        "{\n" +
        "  \"suggestions\": [\n" +
        "    {\n" +
        "      \"name\": \"Example Location\",\n" +
        "      \"type\": \"Restaurant\",\n" +
        "      \"address\": \"123 Highway Rd, City, State\",\n" +
        "      \"reason\": \"Located at the halfway point, perfect for a lunch break\"\n" +
        "    }\n" +
        "  ]\n" +
        "}\n\n" +
        "IMPORTANT: Return ONLY the JSON, no additional text or markdown formatting.";

    /**
     * Prompt template for parsing natural language POI queries into Google Places API place types.
     * Expected response format: JSON with array of place type strings.
     */
    public static final String PARSE_POI_QUERY_PROMPT =
        "You are a location search query parser. Your job is to convert natural language queries " +
        "into Google Places API place type identifiers.\n\n" +
        "User query: \"%s\"\n\n" +
        "Analyze this query and determine which Google Places API place types are most relevant. " +
        "Valid place types include (but are not limited to):\n" +
        "- gas_station\n" +
        "- restaurant\n" +
        "- cafe\n" +
        "- convenience_store\n" +
        "- park\n" +
        "- tourist_attraction\n" +
        "- lodging\n" +
        "- pharmacy\n" +
        "- atm\n" +
        "- bank\n" +
        "- hospital\n" +
        "- parking\n" +
        "- rest_area\n" +
        "- meal_takeaway\n" +
        "- coffee_shop\n\n" +
        "Return 1-3 most relevant place types as a JSON array. " +
        "Place types must be lowercase with underscores (e.g., 'gas_station', not 'Gas Station').\n\n" +
        "Return your response as a JSON object with this exact structure:\n" +
        "{\n" +
        "  \"placeTypes\": [\"gas_station\", \"convenience_store\"]\n" +
        "}\n\n" +
        "Examples:\n" +
        "- \"find me a gas station\" -> [\"gas_station\"]\n" +
        "- \"coffee shop\" -> [\"cafe\", \"coffee_shop\"]\n" +
        "- \"I need gas and food\" -> [\"gas_station\", \"restaurant\"]\n" +
        "- \"pharmacy\" -> [\"pharmacy\"]\n\n" +
        "IMPORTANT: Return ONLY the JSON, no additional text or markdown formatting.";

    /**
     * Prompt template for generating AI-powered POI suggestions along a specific route.
     * This prompt is more detailed and includes route context and specific place types.
     */
    public static final String AI_POI_SUGGESTIONS_PROMPT =
        "You are a travel assistant helping users find points of interest along their route.\n\n" +
        "Route Information:\n" +
        "- Origin: %s\n" +
        "- Destination: %s\n" +
        "- Distance: %s\n" +
        "- Duration: %s\n\n" +
        "Requested place types: %s\n\n" +
        "Please suggest 3-5 specific locations of the requested types that would be convenient stops along this route. " +
        "Focus on places that are:\n" +
        "1. Actually along or very close to the route\n" +
        "2. Well-rated or well-known establishments\n" +
        "3. Strategically spaced along the journey\n\n" +
        "For each suggestion, provide:\n" +
        "- name: The actual or plausible name of a real establishment\n" +
        "- type: The formatted type (e.g., 'Gas Station', 'Restaurant', 'Coffee Shop')\n" +
        "- address: A specific or approximate address along the route\n" +
        "- reason: Why this location is a good stop (consider distance, timing, facilities, ratings)\n\n" +
        "Return your response as a JSON object with this exact structure:\n" +
        "{\n" +
        "  \"suggestions\": [\n" +
        "    {\n" +
        "      \"name\": \"Shell Gas Station\",\n" +
        "      \"type\": \"Gas Station\",\n" +
        "      \"address\": \"123 I-26, Summerville, SC\",\n" +
        "      \"reason\": \"Located about halfway through your route, right off exit 199 with good reviews\"\n" +
        "    }\n" +
        "  ]\n" +
        "}\n\n" +
        "IMPORTANT: Return ONLY the JSON, no additional text or markdown formatting.";
}
