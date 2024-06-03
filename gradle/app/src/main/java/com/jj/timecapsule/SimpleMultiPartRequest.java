package com.jj.timecapsule;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class SimpleMultiPartRequest extends Request<String> {

    private final Response.Listener<String> mListener;
    private final Map<String, String> mStringParts;
    private static final String BOUNDARY = "boundary-" + System.currentTimeMillis();

    public SimpleMultiPartRequest(int method, String url, Map<String, String> stringParts,
                                  Response.Listener<String> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mStringParts = stringParts;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + BOUNDARY;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            // Add string parts
            for (Map.Entry<String, String> entry : mStringParts.entrySet()) {
                writeStringPart(bos, entry.getKey(), entry.getValue());
            }
            // Write end boundary
            bos.write(("--" + BOUNDARY + "--\r\n").getBytes());
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new AuthFailureError("Unsupported Encoding"));
        }
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    private void writeStringPart(ByteArrayOutputStream bos, String name, String value) throws IOException {
        bos.write(("--" + BOUNDARY + "\r\n").getBytes());
        bos.write(("Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n").getBytes());
        bos.write((value + "\r\n").getBytes());
    }
}
