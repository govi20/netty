/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import io.netty.buffer.ChannelBuffer;
import io.netty.buffer.ChannelBuffers;
import io.netty.handler.codec.PrematureChannelClosureException;
import io.netty.handler.codec.embedder.CodecEmbedderException;
import io.netty.handler.codec.embedder.DecoderEmbedder;
import io.netty.handler.codec.embedder.EncoderEmbedder;
import io.netty.util.CharsetUtil;
import org.junit.Test;

public class HttpClientCodecTest {

    private static final String RESPONSE = "HTTP/1.0 200 OK\r\n" + "Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n" + "Content-Type: text/html\r\n" + "Content-Length: 28\r\n" + "\r\n"
            + "<html><body></body></html>\r\n";

    @Test
    public void testFailsNotOnRequestResponse() {
        HttpClientCodec codec = new HttpClientCodec();
        DecoderEmbedder<ChannelBuffer> decoder = new DecoderEmbedder<ChannelBuffer>(codec);
        EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(codec);
        
        encoder.offer(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/"));
        decoder.offer(ChannelBuffers.copiedBuffer(RESPONSE, CharsetUtil.ISO_8859_1));

        encoder.finish();
        decoder.finish();
   
    }
    
    @Test
    public void testFailsOnMissingResponse() {
        HttpClientCodec codec = new HttpClientCodec();
        EncoderEmbedder<ChannelBuffer> encoder = new EncoderEmbedder<ChannelBuffer>(codec);
        
        encoder.offer(new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/"));

        try {
            encoder.finish();
            fail();
        } catch (CodecEmbedderException e) {
            assertTrue(e.getCause() instanceof PrematureChannelClosureException);
        }
        
    }
}
