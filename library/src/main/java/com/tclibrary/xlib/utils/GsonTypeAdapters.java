package com.tclibrary.xlib.utils;

import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by FunTc on 2020/07/09.
 */
public class GsonTypeAdapters {
    
    private GsonTypeAdapters() {}

    public static final TypeAdapter<Number> SHORT = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else if (peek == JsonToken.STRING) {
                String str = in.nextString();
                if (str != null && str.length() != 0) {
                    return Short.parseShort(str);
                }
                return null;
            }
            return (short) in.nextInt();
        }
        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };

    public static final TypeAdapter<Number> INTEGER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else if (peek == JsonToken.STRING) {
                String str = in.nextString();
                if (str != null && str.length() != 0) {
                    return Integer.parseInt(str);
                }
                return null;
            }
            return in.nextInt();
        }
        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };

    public static final TypeAdapter<Number> LONG = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else if (peek == JsonToken.STRING) {
                String str = in.nextString();
                if (str != null && str.length() != 0) {
                    return Long.parseLong(str);
                }
                return null;
            }
            return in.nextLong();
        }
        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };

    public static final TypeAdapter<Number> FLOAT = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else if (peek == JsonToken.STRING) {
                String str = in.nextString();
                if (str != null && str.length() != 0) {
                    return Float.parseFloat(str);
                }
                return null;
            }
            return (float) in.nextDouble();
        }
        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };

    public static final TypeAdapter<Number> DOUBLE = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else if (peek == JsonToken.STRING) {
                String str = in.nextString();
                if (str != null && str.length() != 0) {
                    return Double.parseDouble(str);
                }
                return null;
            }
            return in.nextDouble();
        }
        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };


    /**
     * 把 0，1 也转换成boolean
     */
    public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
        @Override
        public Boolean read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            } else if (peek == JsonToken.STRING) {
                String str = in.nextString();
                return Boolean.parseBoolean(str) || "1".equals(str);
            } else if (peek == JsonToken.NUMBER) {
                int n = in.nextInt();
                return n == 1;
            }
            return in.nextBoolean();
        }
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value);
        }
    };


    /**
     * 适配JsonObject和JsonArray的格式也能转换成String
     */
    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        @Override
        public String read(JsonReader in) throws IOException {
            JsonToken peek = in.peek();
            if (peek == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            /* coerce booleans to strings for backwards compatibility */
            if (peek == JsonToken.BOOLEAN) {
                return Boolean.toString(in.nextBoolean());
            } else if (peek == JsonToken.BEGIN_OBJECT || peek == JsonToken.BEGIN_ARRAY) {
                JsonElement je = TypeAdapters.JSON_ELEMENT.read(in);
                return je.toString();
            }
            return in.nextString();
        }
        @Override
        public void write(JsonWriter out, String value) throws IOException {
            out.value(value);
        }
    };
    
    
    
    
}
