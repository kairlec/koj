package com.kairlec.koj.common


/**
 * **See Also** [RFC2616](https://datatracker.ietf.org/doc/html/rfc2616)
 *
 * Each Status-Code is described below, including a description of which
 * method(s) it can follow and any metainformation required in the
 * response.
 */
@JvmInline
value class HttpStatusCode(val code: Int) {
    companion object {
        /**
         * **See Also** [RFC2616#10.1](https://datatracker.ietf.org/doc/html/rfc2616#section-10.1)
         *
         * This class of status code indicates a provisional response,
         * consisting only of the Status-Line and optional headers, and is
         * terminated by an empty line. There are no required headers for this
         * class of status code. Since HTTP/1.0 did not define any 1xx status
         * codes, servers MUST NOT send a 1xx response to an HTTP/1.0 client
         * except under experimental conditions.
         *
         * A client MUST be prepared to accept one or more 1xx status responses
         * prior to a regular response, even if the client does not expect a 100
         * (Continue) status message. Unexpected 1xx status responses MAY be
         * ignored by a user agent.
         *
         * Proxies MUST forward 1xx responses, unless the connection between the
         * proxy and its client has been closed, or unless the proxy itself
         * requested the generation of the 1xx response. (For example, if a
         * proxy adds a "Expect: 100-continue" field when it forwards a request,
         * then it need not forward the corresponding 100 (Continue)
         * response(s).)
         * */
        object Informational {
            /**
             * **See Also** [RFC2616#10.1.1](https://datatracker.ietf.org/doc/html/rfc2616#section-10.1.1)
             *
             * The client SHOULD continue with its request. This interim response is
             * used to inform the client that the initial part of the request has
             * been received and has not yet been rejected by the server. The client
             * SHOULD continue by sending the remainder of the request or, if the
             * request has already been completed, ignore this response. The server
             * MUST send a final response after the request has been completed. See
             * [section 8.2.3](https://datatracker.ietf.org/doc/html/rfc2616#section-8.2.3) for detailed discussion of the use and handling of this
             * status code.
             */
            val Continue = HttpStatusCode(100)

            /**
             * **See Also** [RFC2616#10.1.2](https://datatracker.ietf.org/doc/html/rfc2616#section-10.1.2)
             *
             * The server understands and is willing to comply with the client's
             * request, via the Upgrade message header field (section 14.42), for a
             * change in the application protocol being used on this connection. The
             * server will switch protocols to those defined by the response's
             * Upgrade header field immediately after the empty line which
             * terminates the 101 response.
             *
             * The protocol SHOULD be switched only when it is advantageous to do
             * so. For example, switching to a newer version of HTTP is advantageous
             * over older versions, and switching to a real-time, synchronous
             * protocol might be advantageous when delivering resources that use
             * such features.
             */
            val SwitchingProtocols = HttpStatusCode(101)

            /**
             * **See Also** [RFC2518#10.1](https://datatracker.ietf.org/doc/html/rfc2518#section-10.1)
             *
             * The 102 (Processing) status code is an interim response used to
             * inform the client that the server has accepted the complete request,
             * but has not yet completed it.  This status code SHOULD only be sent
             * when the server has a reasonable expectation that the request will
             * take significant time to complete. As guidance, if a method is taking
             * longer than 20 seconds (a reasonable, but arbitrary value) to process
             * the server SHOULD return a 102 (Processing) response. The server MUST
             * send a final response after the request has been completed.
             *
             * Methods can potentially take a long period of time to process,
             * especially methods that support the Depth header.  In such cases the
             * client may time-out the connection while waiting for a response.  To
             * prevent this the server may return a 102 (Processing) status code to
             * indicate to the client that the server is still processing the
             * method.
             */
            val Processing = HttpStatusCode(102)

            /**
             * **See Also** [RFC8297#2](https://datatracker.ietf.org/doc/html/rfc8297#section-2)
             *
             * The 103 (Early Hints) informational status code indicates to the
             * client that the server is likely to send a final response with the
             * header fields included in the informational response.
             *
             * Typically, a server will include the header fields sent in a 103
             * (Early Hints) response in the final response as well.  However, there
             * might be cases when this is not desirable, such as when the server
             * learns that the header fields in the 103 (Early Hints) response are
             * not correct before the final response is sent.
             *
             * A client can speculatively evaluate the header fields included in a
             * 103 (Early Hints) response while waiting for the final response.  For
             * example, a client might recognize a Link header field value
             * containing the relation type "preload" and start fetching the target
             * resource.  However, these header fields only provide hints to the
             * client; they do not replace the header fields on the final response.
             *
             * Aside from performance optimizations, such evaluation of the 103
             * (Early Hints) response's header fields MUST NOT affect how the final
             * response is processed.  A client MUST NOT interpret the 103 (Early
             * Hints) response header fields as if they applied to the informational
             * response itself (e.g., as metadata about the 103 (Early Hints)
             * response).
             *
             * A server MAY use a 103 (Early Hints) response to indicate only some
             * of the header fields that are expected to be found in the final
             * response.  A client SHOULD NOT interpret the nonexistence of a header
             * field in a 103 (Early Hints) response as a speculation that the
             * header field is unlikely to be part of the final response.
             *
             * The following example illustrates a typical message exchange that
             * involves a 103 (Early Hints) response.
             * Client request:
             *
             * GET / HTTP/1.1
             * Host: example.com
             *
             *
             * Server response:
             *
             * HTTP/1.1 103 Early Hints
             * Link: </style.css>; rel=preload; as=style
             * Link: </script.js>; rel=preload; as=script
             *
             * HTTP/1.1 200 OK
             * Date: Fri, 26 May 2017 10:02:11 GMT
             * Content-Length: 1234
             * Content-Type: text/html; charset=utf-8
             * Link: </style.css>; rel=preload; as=style
             * Link: </script.js>; rel=preload; as=script
             *
             * <!doctype html>
             * [... rest of the response body is omitted from the example ...]
             *
             * As is the case with any informational response, a server might emit
             * more than one 103 (Early Hints) response prior to sending a final
             * response.  This can happen, for example, when a caching intermediary
             * generates a 103 (Early Hints) response based on the header fields of
             * a stale-cached response, and then forwards a 103 (Early Hints)
             * response and a final response that were sent from the origin server
             * in response to a revalidation request.
             *
             * A server MAY emit multiple 103 (Early Hints) responses with
             * additional header fields as new information becomes available while
             * the request is being processed.  It does not need to repeat the
             * fields that were already emitted, though it doesn't have to exclude
             * them either.  The client can consider any combination of header
             * fields received in multiple 103 (Early Hints) responses when
             * anticipating the list of header fields expected in the final
             * response.
             *
             * The following example illustrates a series of responses that a server
             * might emit.  In the example, the server uses two 103 (Early Hints)
             * responses to notify the client that it is likely to send three Link
             * header fields in the final response.  Two of the three expected
             * header fields are found in the final response.  The other header
             * field is replaced by another Link header field that contains a
             * different value.
             * HTTP/1.1 103 Early Hints
             * Link: </main.css>; rel=preload; as=style
             *
             * HTTP/1.1 103 Early Hints
             * Link: </style.css>; rel=preload; as=style
             * Link: </script.js>; rel=preload; as=script
             *
             * HTTP/1.1 200 OK
             * Date: Fri, 26 May 2017 10:02:11 GMT
             * Content-Length: 1234
             * Content-Type: text/html; charset=utf-8
             * Link: </main.css>; rel=preload; as=style
             * Link: </newstyle.css>; rel=preload; as=style
             * Link: </script.js>; rel=preload; as=script
             *
             * <!doctype html>
             * [... rest of the response body is omitted from the example ...]
             */
            val EarlyHints = HttpStatusCode(103)
        }

        /**
         * **See Also** [RFC2616#10.2](https://datatracker.ietf.org/doc/html/rfc2616#section-10.2)
         *
         * This class of status code indicates that the client's request was
         * successfully received, understood, and accepted.
         */
        object Successful {
            /**
             * **See Also** [RFC2616#10.2.1](https://datatracker.ietf.org/doc/html/rfc2616#section-10.2.1)
             *
             *  The request has succeeded. The information returned with the response
             *  is dependent on the method used in the request, for example:
             *
             *  GET    an entity corresponding to the requested resource is sent in
             *  the response;
             *
             *  HEAD   the entity-header fields corresponding to the requested
             *  resource are sent in the response without any message-body;
             *
             *  POST   an entity describing or containing the result of the action;
             *
             *  TRACE  an entity containing the request message as received by the
             *  end server.
             */
            val OK = HttpStatusCode(200)

            /**
             * **See Also** [RFC2616#10.2.2](https://datatracker.ietf.org/doc/html/rfc2616#section-10.2.2)
             *
             * The request has been fulfilled and resulted in a new resource being
             * created. The newly created resource can be referenced by the URI(s)
             * returned in the entity of the response, with the most specific URI
             * for the resource given by a Location header field. The response
             * SHOULD include an entity containing a list of resource
             * characteristics and location(s) from which the user or user agent can
             * choose the one most appropriate. The entity format is specified by
             * the media type given in the Content-Type header field. The origin
             * server MUST create the resource before returning the 201 status code.
             * If the action cannot be carried out immediately, the server SHOULD
             * respond with 202 (Accepted) response instead.
             *
             * A 201 response MAY contain an ETag response header field indicating
             * the current value of the entity tag for the requested variant just
             * created, see [section 14.19](https://datatracker.ietf.org/doc/html/rfc2616#section-14.19).
             */
            val Created = HttpStatusCode(201)


            /**
             * **See Also** [RFC2616#10.2.3](https://datatracker.ietf.org/doc/html/rfc2616#section-10.2.3)
             *
             * The request has been accepted for processing, but the processing has
             * not been completed.  The request might or might not eventually be
             * acted upon, as it might be disallowed when processing actually takes
             * place. There is no facility for re-sending a status code from an
             * asynchronous operation such as this.
             *
             * The 202 response is intentionally non-committal. Its purpose is to
             * allow a server to accept a request for some other process (perhaps a
             * batch-oriented process that is only run once per day) without
             * requiring that the user agent's connection to the server persist
             * until the process is completed. The entity returned with this
             * response SHOULD include an indication of the request's current status
             * and either a pointer to a status monitor or some estimate of when the
             * user can expect the request to be fulfilled.
             */
            val Accepted = HttpStatusCode(202)

            /**
             * **See Also** [RFC2616#10.2.4](https://datatracker.ietf.org/doc/html/rfc2616#section-10.2.4)
             *
             * The returned metainformation in the entity-header is not the
             * definitive set as available from the origin server, but is gathered
             * from a local or a third-party copy. The set presented MAY be a subset
             * or superset of the original version. For example, including local
             * annotation information about the resource might result in a superset
             * of the metainformation known by the origin server. Use of this
             * response code is not required and is only appropriate when the
             * response would otherwise be 200 (OK).
             */
            val NonAuthoritativeInformation = HttpStatusCode(203)

            /**
             * **See Also** [RFC2616#10.2.5](https://datatracker.ietf.org/doc/html/rfc2616#section-10.2.5)
             *
             * The server has fulfilled the request but does not need to return an
             * entity-body, and might want to return updated metainformation. The
             * response MAY include new or updated metainformation in the form of
             * entity-headers, which if present SHOULD be associated with the
             * requested variant.
             *
             * If the client is a user agent, it SHOULD NOT change its document view
             * from that which caused the request to be sent. This response is
             * primarily intended to allow input for actions to take place without
             * causing a change to the user agent's active document view, although
             * any new or updated metainformation SHOULD be applied to the document
             * currently in the user agent's active view.
             *
             * The 204 response MUST NOT include a message-body, and thus is always
             * terminated by the first empty line after the header fields.
             */
            val NoContent = HttpStatusCode(204)

            /**
             * **See Also** [RFC2616#10.2.6](https://datatracker.ietf.org/doc/html/rfc2616#section-10.2.6)
             *
             * The server has fulfilled the request and the user agent SHOULD reset
             * the document view which caused the request to be sent. This response
             * is primarily intended to allow input for actions to take place via
             * user input, followed by a clearing of the form in which the input is
             * given so that the user can easily initiate another input action. The
             * response MUST NOT include an entity.
             */
            val ResetContent = HttpStatusCode(205)

            /**
             * **See Also** [RFC2616#10.2.7](https://datatracker.ietf.org/doc/html/rfc2616#section-10.2.7)
             *
             * **See Also** [RFC7233](https://datatracker.ietf.org/doc/html/rfc7233)
             *
             * The server has fulfilled the partial GET request for the resource.
             * The request MUST have included a Range header field (section 14.35)
             * indicating the desired range, and MAY have included an If-Range
             * header field (section 14.27) to make the request conditional.
             *
             * The response MUST include the following header fields:
             *
             * - Either a Content-Range header field (section 14.16) indicating
             * the range included with this response, or a multipart/byteranges
             * Content-Type including Content-Range fields for each part. If a
             * Content-Length header field is present in the response, its
             * value MUST match the actual number of OCTETs transmitted in the
             * message-body.
             *
             * - Date
             *
             * - ETag and/or Content-Location, if the header would have been sent
             * in a 200 response to the same request
             *
             * - Expires, Cache-Control, and/or Vary, if the field-value might
             * differ from that sent in any previous response for the same
             * variant
             *
             * If the 206 response is the result of an If-Range request that used a
             * strong cache validator (see section 13.3.3), the response SHOULD NOT
             * include other entity-headers. If the response is the result of an
             * If-Range request that used a weak validator, the response MUST NOT
             * include other entity-headers; this prevents inconsistencies between
             * cached entity-bodies and updated headers. Otherwise, the response
             * MUST include all of the entity-headers that would have been returned
             * with a 200 (OK) response to the same request.
             *
             * A cache MUST NOT combine a 206 response with other previously cached
             * content if the ETag or Last-Modified headers do not match exactly,
             * see 13.5.4.
             *
             * A cache that does not support the Range and Content-Range headers
             * MUST NOT cache 206 (Partial) responses.
             */
            val PartialContent = HttpStatusCode(206)

            /**
             * **See Also** [RFC4918#11.1](https://datatracker.ietf.org/doc/html/rfc4918#section-11.1)
             *
             * The 207 (Multi-Status) status code provides status for multiple
             * independent operations (see [Section 13](https://datatracker.ietf.org/doc/html/rfc4918#section-13) for more information).
             */
            val MultiStatus = HttpStatusCode(207)

            /**
             * **See Also** [RFC5842#7.1](https://datatracker.ietf.org/doc/html/rfc5842#section-7.1)
             *
             * The 208 (Already Reported) status code can be used inside a DAV:
             * propstat response element to avoid enumerating the internal members
             * of multiple bindings to the same collection repeatedly.  For each
             * binding to a collection inside the request's scope, only one will be
             * reported with a 200 status, while subsequent DAV:response elements
             * for all other bindings will use the 208 status, and no DAV:response
             * elements for their descendants are included.
             * Note that the 208 status will only occur for "Depth: infinity"
             * requests, and that it is of particular importance when the multiple
             * collection bindings cause a bind loop as discussed in [Section 2.2](https://datatracker.ietf.org/doc/html/rfc5842#section-2.2).
             *
             * A client can request the DAV:resource-id property in a PROPFIND
             * request to guarantee that they can accurately reconstruct the binding
             * structure of a collection with multiple bindings to a single
             * resource.
             *
             * For backward compatibility with clients not aware of the 208 status
             * code appearing in multistatus response bodies, it SHOULD NOT be used
             * unless the client has signaled support for this specification using
             * the "DAV" request header (see [Section 8.2](https://datatracker.ietf.org/doc/html/rfc5842#section-8.2)).  Instead, a 508 status
             * should be returned when a binding loop is discovered.  This allows
             * the server to return the 508 as the top-level return status, if it
             * discovers it before it started the response, or in the middle of a
             * multistatus, if it discovers it in the middle of streaming out a
             * multistatus response.
             */
            val AlreadyReported = HttpStatusCode(208)

            /**
             * **See Also** [RFC3229#10.4.1](https://datatracker.ietf.org/doc/html/rfc3229#section-10.4.1)
             *
             * The server has fulfilled a GET request for the resource, and the
             * response is a representation of the result of one or more instance-
             * manipulations applied to the current instance.  The actual current
             * instance might not be available except by combining this response
             * with other previous or future responses, as appropriate for the
             * specific instance-manipulation(s).  If so, the headers of the
             * resulting instance are the result of combining the headers from the
             * status-226 response and the other instances, following the rules in
             * [section 13.5.3](https://datatracker.ietf.org/doc/html/rfc3229#section-13.5.3) of the HTTP/1.1 specification [10].
             *
             * The request MUST have included an A-IM header field listing at least
             * one instance-manipulation.  The response MUST include an Etag header
             * field giving the entity tag of the current instance.
             *
             * A response received with a status code of 226 MAY be stored by a
             * cache and used in reply to a subsequent request, subject to the HTTP
             * expiration mechanism and any Cache-Control headers, and to the
             * requirements in [section 10.6](https://datatracker.ietf.org/doc/html/rfc3229#section-10.6).
             *
             * A response received with a status code of 226 MAY be used by a cache,
             * in conjunction with a cache entry for the base instance, to create a
             * cache entry for the current instance.
             */
            val IMUsed = HttpStatusCode(226)
        }

        /**
         * **See Also** [RFC2616#10.3](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3)
         *
         * This class of status code indicates that further action needs to be
         * taken by the user agent in order to fulfill the request.  The action
         * required MAY be carried out by the user agent without interaction
         * with the user if and only if the method used in the second request is
         * GET or HEAD. A client SHOULD detect infinite redirection loops, since
         * such loops generate network traffic for each redirection.
         *
         * Note: previous versions of this specification recommended a
         * maximum of five redirections. Content developers should be aware
         * that there might be clients that implement such a fixed
         * limitation.
         */
        object Redirection {
            /**
             * **See Also** [RFC2616#10.3.1](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3.1)
             *
             * The requested resource corresponds to any one of a set of
             * representations, each with its own specific location, and agent-
             * driven negotiation information ([section 12](https://datatracker.ietf.org/doc/html/rfc2616#section-12)) is being provided so that
             * the user (or user agent) can select a preferred representation and
             * redirect its request to that location.
             *
             * Unless it was a HEAD request, the response SHOULD include an entity
             * containing a list of resource characteristics and location(s) from
             * which the user or user agent can choose the one most appropriate. The
             * entity format is specified by the media type given in the Content-
             * Type header field. Depending upon the format and the capabilities of
             * the user agent, selection of the most appropriate choice MAY be
             * performed automatically. However, this specification does not define
             * any standard for such automatic selection.
             *
             * If the server has a preferred choice of representation, it SHOULD
             * include the specific URI for that representation in the Location
             * field; user agents MAY use the Location field value for automatic
             * redirection. This response is cacheable unless indicated otherwise.
             */
            val MultipleChoices = HttpStatusCode(300)

            /**
             * **See Also** [RFC2616#10.3.2](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3.2)
             *
             * The requested resource has been assigned a new permanent URI and any
             * future references to this resource SHOULD use one of the returned
             * URIs.  Clients with link editing capabilities ought to automatically
             * re-link references to the Request-URI to one or more of the new
             * references returned by the server, where possible. This response is
             * cacheable unless indicated otherwise.
             *
             * The new permanent URI SHOULD be given by the Location field in the
             * response. Unless the request method was HEAD, the entity of the
             * response SHOULD contain a short hypertext note with a hyperlink to
             * the new URI(s).
             *
             * If the 301 status code is received in response to a request other
             * than GET or HEAD, the user agent MUST NOT automatically redirect the
             * request unless it can be confirmed by the user, since this might
             * change the conditions under which the request was issued.
             *
             * Note: When automatically redirecting a POST request after
             * receiving a 301 status code, some existing HTTP/1.0 user agents
             * will erroneously change it into a GET request.
             */
            val MovedPermanently = HttpStatusCode(301)

            /**
             * **See Also** [RFC2616#10.3.3](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3.3)
             *
             * The requested resource resides temporarily under a different URI.
             * Since the redirection might be altered on occasion, the client SHOULD
             * continue to use the Request-URI for future requests.  This response
             * is only cacheable if indicated by a Cache-Control or Expires header
             * field.
             *
             * The temporary URI SHOULD be given by the Location field in the
             * response. Unless the request method was HEAD, the entity of the
             * response SHOULD contain a short hypertext note with a hyperlink to
             * the new URI(s).
             * If the 302 status code is received in response to a request other
             * than GET or HEAD, the user agent MUST NOT automatically redirect the
             * request unless it can be confirmed by the user, since this might
             * change the conditions under which the request was issued.
             *
             * Note: [RFC 1945](https://datatracker.ietf.org/doc/html/rfc1945) and [RFC 2068](https://datatracker.ietf.org/doc/html/rfc2068) specify that the client is not allowed
             * to change the method on the redirected request.  However, most
             * existing user agent implementations treat 302 as if it were a 303
             * response, performing a GET on the Location field-value regardless
             * of the original request method. The status codes 303 and 307 have
             * been added for servers that wish to make unambiguously clear which
             * kind of reaction is expected of the client.
             */
            val Found = HttpStatusCode(302)

            /**
             * **See Also** [RFC2616#10.3.4](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3.4)
             *
             * The response to the request can be found under a different URI and
             * SHOULD be retrieved using a GET method on that resource. This method
             * exists primarily to allow the output of a POST-activated script to
             * redirect the user agent to a selected resource. The new URI is not a
             * substitute reference for the originally requested resource. The 303
             * response MUST NOT be cached, but the response to the second
             * (redirected) request might be cacheable.
             *
             * The different URI SHOULD be given by the Location field in the
             * response. Unless the request method was HEAD, the entity of the
             * response SHOULD contain a short hypertext note with a hyperlink to
             * the new URI(s).
             *
             * Note: Many pre-HTTP/1.1 user agents do not understand the 303
             * status. When interoperability with such clients is a concern, the
             * 302 status code may be used instead, since most user agents react
             * to a 302 response as described here for 303.
             */
            val SeeOther = HttpStatusCode(303)

            /**
             * **See Also** [RFC2616#10.3.5](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3.5)
             *
             * If the client has performed a conditional GET request and access is
             * allowed, but the document has not been modified, the server SHOULD
             * respond with this status code. The 304 response MUST NOT contain a
             * message-body, and thus is always terminated by the first empty line
             * after the header fields.
             *
             * The response MUST include the following header fields:
             *
             * - Date, unless its omission is required by [section 14.18.1](https://datatracker.ietf.org/doc/html/rfc2616#section-14.18.1)
             *
             * If a clockless origin server obeys these rules, and proxies and
             * clients add their own Date to any response received without one (as
             * already specified by [RFC 2068 section 14.19](https://datatracker.ietf.org/doc/html/rfc2068#section-14.19)), caches will operate
             * correctly.
             *
             * - ETag and/or Content-Location, if the header would have been sent
             * in a 200 response to the same request
             *
             * - Expires, Cache-Control, and/or Vary, if the field-value might
             * differ from that sent in any previous response for the same
             * variant
             *
             * If the conditional GET used a strong cache validator (see [section 13.3.3](https://datatracker.ietf.org/doc/html/rfc2616#section-13.3.3)),
             * the response SHOULD NOT include other entity-headers.
             * Otherwise (i.e., the conditional GET used a weak validator), the
             * response MUST NOT include other entity-headers; this prevents
             * inconsistencies between cached entity-bodies and updated headers.
             *
             * If a 304 response indicates an entity not currently cached, then the
             * cache MUST disregard the response and repeat the request without the
             * conditional.
             *
             * If a cache uses a received 304 response to update a cache entry, the
             * cache MUST update the entry to reflect any new field values given in
             * the response.
             */
            val NotModified = HttpStatusCode(304)

            /**
             * **See Also** [RFC2616#10.3.6](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3.6)
             *
             * The requested resource MUST be accessed through the proxy given by
             * the Location field. The Location field gives the URI of the proxy.
             * The recipient is expected to repeat this single request via the
             * proxy. 305 responses MUST only be generated by origin servers.
             *
             * Note: [RFC 2068](https://datatracker.ietf.org/doc/html/rfc2068) was not clear that 305 was intended to redirect a
             * single request, and to be generated by origin servers only.  Not
             * observing these limitations has significant security consequences.
             */
            val UseProxy = HttpStatusCode(305)

            /**
             * **See Also** [RFC2616#10.3.7](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3.7)
             *
             * The 306 status code was used in a previous version of the
             * specification, is no longer used, and the code is reserved.
             */
            @Deprecated("The 306 status code was used in a previous version of the specification, is no longer used, and the code is reserved.")
            val SwitchProxy = HttpStatusCode(306)

            /**
             * **See Also** [RFC2616#10.3.8](https://datatracker.ietf.org/doc/html/rfc2616#section-10.3.8)
             *
             * The requested resource resides temporarily under a different URI.
             * Since the redirection MAY be altered on occasion, the client SHOULD
             * continue to use the Request-URI for future requests.  This response
             * is only cacheable if indicated by a Cache-Control or Expires header
             * field.
             *
             * The temporary URI SHOULD be given by the Location field in the
             * response. Unless the request method was HEAD, the entity of the
             * response SHOULD contain a short hypertext note with a hyperlink to
             * the new URI(s) , since many pre-HTTP/1.1 user agents do not
             * understand the 307 status. Therefore, the note SHOULD contain the
             * information necessary for a user to repeat the original request on
             * the new URI.
             *
             * If the 307 status code is received in response to a request other
             * than GET or HEAD, the user agent MUST NOT automatically redirect the
             * request unless it can be confirmed by the user, since this might
             * change the conditions under which the request was issued.
             */
            val TemporaryRedirect = HttpStatusCode(307)

            /**
             * **See Also** [RFC7538](https://datatracker.ietf.org/doc/html/rfc7538)
             *
             * The 308 (Permanent Redirect) status code indicates that the target
             * resource has been assigned a new permanent URI and any future
             * references to this resource ought to use one of the enclosed URIs.
             * Clients with link editing capabilities ought to automatically re-link
             * references to the effective request URI ([Section 5.5 of RFC7230](https://datatracker.ietf.org/doc/html/rfc7230#section-5.5)) to
             * one or more of the new references sent by the server, where possible.
             *
             * The server SHOULD generate a Location header field ([RFC7231, Section 7.1.2](https://datatracker.ietf.org/doc/html/rfc7231#section-7.1.2))
             * in the response containing a preferred URI reference
             * for the new permanent URI.  The user agent MAY use the Location field
             * value for automatic redirection.  The server's response payload
             * usually contains a short hypertext note with a hyperlink to the new
             * URI(s).
             *
             * A 308 response is cacheable by default; i.e., unless otherwise
             * indicated by the method definition or explicit cache controls (see
             * [RFC7234, Section 4.2.2](https://datatracker.ietf.org/doc/html/rfc7234#section-4.2.2)).
             *
             * Note: This status code is similar to 301 (Moved Permanently)
             * ([RFC7231, Section 6.4.2](https://datatracker.ietf.org/doc/html/rfc7231#section-6.4.2)), except that it does not allow changing
             * the request method from POST to GET.
             */
            val PermanentRedirect = HttpStatusCode(308)
        }

        /**
         * **See Also** [RFC2616#10.4](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4)
         *
         * The 4xx class of status code is intended for cases in which the
         * client seems to have erred. Except when responding to a HEAD request,
         * the server SHOULD include an entity containing an explanation of the
         * error situation, and whether it is a temporary or permanent
         * condition. These status codes are applicable to any request method.
         * User agents SHOULD display any included entity to the user.
         *
         * If the client is sending data, a server implementation using TCP
         * SHOULD be careful to ensure that the client acknowledges receipt of
         * the packet(s) containing the response, before the server closes the
         * input connection. If the client continues sending data to the server
         * after the close, the server's TCP stack will send a reset packet to
         * the client, which may erase the client's unacknowledged input buffers
         * before they can be read and interpreted by the HTTP application.
         */
        object ClientError {
            /**
             * **See Also** [RFC2616#10.4.1](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.1)
             *
             * The request could not be understood by the server due to malformed
             * syntax. The client SHOULD NOT repeat the request without
             * modifications.
             */
            val BadRequest = HttpStatusCode(400)

            /**
             * **See Also** [RFC2616#10.4.2](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.2)
             *
             * The request requires user authentication. The response MUST include a
             * WWW-Authenticate header field ([section 14.47](https://datatracker.ietf.org/doc/html/rfc2616#section-14.47)) containing a challenge
             * applicable to the requested resource. The client MAY repeat the
             * request with a suitable Authorization header field ([section 14.8](https://datatracker.ietf.org/doc/html/rfc2616#section-14.8)). If
             * the request already included Authorization credentials, then the 401
             * response indicates that authorization has been refused for those
             * credentials. If the 401 response contains the same challenge as the
             * prior response, and the user agent has already attempted
             * authentication at least once, then the user SHOULD be presented the
             * entity that was given in the response, since that entity might
             * include relevant diagnostic information. HTTP access authentication
             * is explained in "HTTP Authentication: Basic and Digest Access
             * Authentication" [43](https://datatracker.ietf.org/doc/html/rfc2616#ref-43).
             */
            val Unauthorized = HttpStatusCode(401)

            /**
             * **See Also** [RFC2616#10.4.3](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.3)
             *
             * This code is reserved for future use.
             *
             * Note:
             * The original intention was that this code might be used as part of
             * some form of digital cash or micropayment scheme, as proposed, for
             * example, by GNU Taler, but that has not yet happened, and this code
             * is not widely used. Google Developers API uses this status if a
             * particular developer has exceeded the daily limit on requests.
             * Sipgate uses this code if an account does not have sufficient funds
             * to start a call. Shopify uses this code when the store has not paid
             * their fees and is temporarily disabled. Stripe uses this code for
             * failed payments where parameters were correct, for example blocked
             * fraudulent payments.
             */
            val PaymentRequired = HttpStatusCode(402)

            /**
             * **See Also** [RFC2616#10.4.4](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.4)
             *
             * The server understood the request, but is refusing to fulfill it.
             * Authorization will not help and the request SHOULD NOT be repeated.
             * If the request method was not HEAD and the server wishes to make
             * public why the request has not been fulfilled, it SHOULD describe the
             * reason for the refusal in the entity.  If the server does not wish to
             * make this information available to the client, the status code 404
             * (Not Found) can be used instead.
             */
            val Forbidden = HttpStatusCode(403)

            /**
             * **See Also** [RFC2616#10.4.5](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.5)
             *
             * The server has not found anything matching the Request-URI. No
             * indication is given of whether the condition is temporary or
             * permanent. The 410 (Gone) status code SHOULD be used if the server
             * knows, through some internally configurable mechanism, that an old
             * resource is permanently unavailable and has no forwarding address.
             * This status code is commonly used when the server does not wish to
             * reveal exactly why the request has been refused, or when no other
             * response is applicable.
             */
            val NotFound = HttpStatusCode(404)

            /**
             * **See Also** [RFC2616#10.4.6](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.6)
             *
             * The method specified in the Request-Line is not allowed for the
             * resource identified by the Request-URI. The response MUST include an
             * Allow header containing a list of valid methods for the requested
             * resource.
             */
            val MethodNotAllowed = HttpStatusCode(405)

            /**
             * **See Also** [RFC2616#10.4.7](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.7)
             *
             * The resource identified by the request is only capable of generating
             * response entities which have content characteristics not acceptable
             * according to the accept headers sent in the request.
             *
             * Unless it was a HEAD request, the response SHOULD include an entity
             * containing a list of available entity characteristics and location(s)
             * from which the user or user agent can choose the one most
             * appropriate. The entity format is specified by the media type given
             * in the Content-Type header field. Depending upon the format and the
             * capabilities of the user agent, selection of the most appropriate
             * choice MAY be performed automatically. However, this specification
             * does not define any standard for such automatic selection.
             *
             * Note: HTTP/1.1 servers are allowed to return responses which are
             * not acceptable according to the accept headers sent in the
             * request. In some cases, this may even be preferable to sending a
             * 406 response. User agents are encouraged to inspect the headers of
             * an incoming response to determine if it is acceptable.
             *
             * If the response could be unacceptable, a user agent SHOULD
             * temporarily stop receipt of more data and query the user for a
             * decision on further actions.
             */
            val NotAcceptable = HttpStatusCode(406)

            /**
             * **See Also** [RFC2616#10.4.8](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.8)
             *
             * This code is similar to 401 (Unauthorized), but indicates that the
             * client must first authenticate itself with the proxy. The proxy MUST
             * return a Proxy-Authenticate header field ([section 14.33](https://datatracker.ietf.org/doc/html/rfc2616#section-14.33)) containing a
             * challenge applicable to the proxy for the requested resource. The
             * client MAY repeat the request with a suitable Proxy-Authorization
             * header field ([section 14.34](https://datatracker.ietf.org/doc/html/rfc2616#section-14.34)). HTTP access authentication is explained
             * in "HTTP Authentication: Basic and Digest Access Authentication"
             * [43](https://datatracker.ietf.org/doc/html/rfc2616#ref-43).
             */
            val ProxyAuthenticationRequired = HttpStatusCode(407)

            /**
             * **See Also** [RFC2616#10.4.9](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.9)
             *
             * The client did not produce a request within the time that the server
             * was prepared to wait. The client MAY repeat the request without
             * modifications at any later time.
             */
            val RequestTimeout = HttpStatusCode(408)

            /**
             * **See Also** [RFC2616#10.4.10](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.10)
             *
             * The request could not be completed due to a conflict with the current
             * state of the resource. This code is only allowed in situations where
             * it is expected that the user might be able to resolve the conflict
             * and resubmit the request. The response body SHOULD include enough
             * information for the user to recognize the source of the conflict.
             * Ideally, the response entity would include enough information for the
             * user or user agent to fix the problem; however, that might not be
             * possible and is not required.
             *
             * Conflicts are most likely to occur in response to a PUT request. For
             * example, if versioning were being used and the entity being PUT
             * included changes to a resource which conflict with those made by an
             * earlier (third-party) request, the server might use the 409 response
             * to indicate that it can't complete the request. In this case, the
             * response entity would likely contain a list of the differences
             * between the two versions in a format defined by the response
             * Content-Type.
             */
            val Conflict = HttpStatusCode(409)

            /**
             * **See Also** [RFC2616#10.4.11](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.11)
             *
             * The requested resource is no longer available at the server and no
             * forwarding address is known. This condition is expected to be
             * considered permanent. Clients with link editing capabilities SHOULD
             * delete references to the Request-URI after user approval. If the
             * server does not know, or has no facility to determine, whether or not
             * the condition is permanent, the status code 404 (Not Found) SHOULD be
             * used instead. This response is cacheable unless indicated otherwise.
             *
             * The 410 response is primarily intended to assist the task of web
             * maintenance by notifying the recipient that the resource is
             * intentionally unavailable and that the server owners desire that
             * remote links to that resource be removed. Such an event is common for
             * limited-time, promotional services and for resources belonging to
             * individuals no longer working at the server's site. It is not
             * necessary to mark all permanently unavailable resources as "gone" or
             * to keep the mark for any length of time -- that is left to the
             * discretion of the server owner.
             */
            val Gone = HttpStatusCode(410)

            /**
             * **See Also** [RFC2616#10.4.12](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.12)
             *
             * The server refuses to accept the request without a defined Content-
             * Length. The client MAY repeat the request if it adds a valid
             * Content-Length header field containing the length of the message-body
             * in the request message.
             */
            val LengthRequired = HttpStatusCode(411)

            /**
             * **See Also** [RFC2616#10.4.13](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.13)
             *
             * The precondition given in one or more of the request-header fields
             * evaluated to false when it was tested on the server. This response
             * code allows the client to place preconditions on the current resource
             * metainformation (header field data) and thus prevent the requested
             * method from being applied to a resource other than the one intended.
             */
            val PreconditionFailed = HttpStatusCode(412)

            /**
             * **See Also** [RFC2616#10.4.14](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.14)
             *
             * The server is refusing to process a request because the request
             * entity is larger than the server is willing or able to process. The
             * server MAY close the connection to prevent the client from continuing
             * the request.
             *
             * If the condition is temporary, the server SHOULD include a Retry-
             * After header field to indicate that it is temporary and after what
             * time the client MAY try again.
             */
            val RequestEntityTooLarge = HttpStatusCode(413)

            /**
             * **See Also** [RFC2616#10.4.15](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.15)
             *
             * The server is refusing to service the request because the Request-URI
             * is longer than the server is willing to interpret. This rare
             * condition is only likely to occur when a client has improperly
             * converted a POST request to a GET request with long query
             * information, when the client has descended into a URI "black hole" of
             * redirection (e.g., a redirected URI prefix that points to a suffix of
             * itself), or when the server is under attack by a client attempting to
             * exploit security holes present in some servers using fixed-length
             * buffers for reading or manipulating the Request-URI.
             */
            val RequestURITooLong = HttpStatusCode(414)

            /**
             * **See Also** [RFC2616#10.4.16](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.16)
             *
             * The server is refusing to service the request because the entity of
             * the request is in a format not supported by the requested resource
             * for the requested method.
             */
            val UnsupportedMediaType = HttpStatusCode(415)

            /**
             * **See Also** [RFC2616#10.4.17](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.17)
             *
             * A server SHOULD return a response with this status code if a request
             * included a Range request-header field ([section 14.35](https://datatracker.ietf.org/doc/html/rfc2616#section-14.35)), and none of
             * the range-specifier values in this field overlap the current extent
             * of the selected resource, and the request did not include an If-Range
             * request-header field. (For byte-ranges, this means that the first-
             * byte-pos of all of the byte-range-spec values were greater than the
             * current length of the selected resource.)
             *
             * When this status code is returned for a byte-range request, the
             * response SHOULD include a Content-Range entity-header field
             * specifying the current length of the selected resource (see [section 14.16](https://datatracker.ietf.org/doc/html/rfc2616#section-14.16)).
             * This response MUST NOT use the multipart/byteranges content-
             * type.
             */
            val RequestedRangeNotSatisfiable = HttpStatusCode(416)

            /**
             * **See Also** [RFC2616#10.4.18](https://datatracker.ietf.org/doc/html/rfc2616#section-10.4.18)
             *
             * The expectation given in an Expect request-header field (see [section 14.20](https://datatracker.ietf.org/doc/html/rfc2616#section-14.20))
             * could not be met by this server, or, if the server is a proxy,
             * the server has unambiguous evidence that the request could not be met
             * by the next-hop server.
             */
            val ExpectationFailed = HttpStatusCode(417)

            /**
             * **See Also** [RFC2324#2.3.2](https://datatracker.ietf.org/doc/html/rfc2324#section-2.3.2)
             *
             * Any attempt to brew coffee with a teapot should result in the error
             * code "418 I'm a teapot". The resulting entity body MAY be short and
             * stout.
             *
             * Note:
             * This code was defined in 1998 as one of the traditional IETF April Fools'
             * jokes, in RFC 2324, Hyper Text Coffee Pot Control Protocol, and is not
             * expected to be implemented by actual HTTP servers. The RFC specifies this
             * code should be returned by teapots requested to brew coffee. This HTTP
             * status is used as an Easter egg in some websites, such as Google.com's
             * I'm a teapot easter egg.
             */
            val ImAteapot = HttpStatusCode(418)

            /**
             * **See Also** [RFC7540#9.1.2](https://datatracker.ietf.org/doc/html/rfc7540#section-9.1.2)
             *
             * The 421 (Misdirected Request) status code indicates that the request
             * was directed at a server that is not able to produce a response.
             * This can be sent by a server that is not configured to produce
             * responses for the combination of scheme and authority that are
             * included in the request URI.
             *
             * Clients receiving a 421 (Misdirected Request) response from a server
             * MAY retry the request -- whether the request method is idempotent or
             * not -- over a different connection.  This is possible if a connection
             * is reused ([Section 9.1.1](https://datatracker.ietf.org/doc/html/rfc7540#section-9.1.1)) or if an alternative service is selected
             * [ALT-SVC](https://datatracker.ietf.org/doc/html/rfc7540#ref-ALT-SVC).
             *
             * This status code MUST NOT be generated by proxies.
             *
             * A 421 response is cacheable by default, i.e., unless otherwise
             * indicated by the method definition or explicit cache controls (see
             * [Section 4.2.2 of RFC7234](https://datatracker.ietf.org/doc/html/rfc7234#section-4.2.2)).
             */
            val MisdirectedRequest = HttpStatusCode(421)

            /**
             * **See Also** [RFC4918#11.2](https://datatracker.ietf.org/doc/html/rfc4918#section-11.2)
             *
             * The 422 (Unprocessable Entity) status code means the server
             * understands the content type of the request entity (hence a
             * 415(Unsupported Media Type) status code is inappropriate), and the
             * syntax of the request entity is correct (thus a 400 (Bad Request)
             * status code is inappropriate) but was unable to process the contained
             * instructions.  For example, this error condition may occur if an XML
             * request body contains well-formed (i.e., syntactically correct), but
             * semantically erroneous, XML instructions.
             */
            val UnprocessableEntity = HttpStatusCode(422)

            /**
             * **See Also** [RFC4918#11.3](https://datatracker.ietf.org/doc/html/rfc4918#section-11.3)
             *
             * The 423 (Locked) status code means the source or destination resource
             * of a method is locked.  This response SHOULD contain an appropriate
             * precondition or postcondition code, such as 'lock-token-submitted' or
             * 'no-conflicting-lock'.
             */
            val Locked = HttpStatusCode(423)

            /**
             * **See Also** [RFC4918#11.4](https://datatracker.ietf.org/doc/html/rfc4918#section-11.4)
             *
             * The 424 (Failed Dependency) status code means that the method could
             * not be performed on the resource because the requested action
             * depended on another action and that action failed.  For example, if a
             * command in a PROPPATCH method fails, then, at minimum, the rest of
             * the commands will also fail with 424 (Failed Dependency).
             */
            val FailedDependency = HttpStatusCode(424)

            /**
             * **See Also** [RFC8470#5.2](https://datatracker.ietf.org/doc/html/rfc8470#section-5.2)
             *
             * A 425 (Too Early) status code indicates that the server is unwilling
             * to risk processing a request that might be replayed.
             *
             * User agents that send a request in early data are expected to retry
             * the request when receiving a 425 (Too Early) response status code.  A
             * user agent SHOULD retry automatically, but any retries MUST NOT be
             * sent in early data.
             *
             * In all cases, an intermediary can forward a 425 (Too Early) status
             * code.  Intermediaries MUST forward a 425 (Too Early) status code if
             * the request that it received and forwarded contained an Early-Data
             * header field.  Otherwise, an intermediary that receives a request in
             * early data MAY automatically retry that request in response to a 425
             * (Too Early) status code, but it MUST wait for the TLS handshake to
             * complete on the connection where it received the request.
             *
             * The server cannot assume that a client is able to retry a request
             * unless the request is received in early data or the Early-Data header
             * field is set to "1".  A server SHOULD NOT emit the 425 status code
             * unless one of these conditions is met.
             *
             * The 425 (Too Early) status code is not cacheable by default.  Its
             * payload is not the representation of any identified resource.
             */
            val TooEarly = HttpStatusCode(425)

            /**
             * **See Also** [RFC2817](https://datatracker.ietf.org/doc/html/rfc2817)
             *
             * The client should switch to a different protocol such as TLS/1.3,
             * given in the Upgrade header field.
             */
            val UpgradeRequired = HttpStatusCode(426)

            /**
             * **See Also** [RFC6585#3](https://datatracker.ietf.org/doc/html/rfc6585#section-3)
             *
             * The 428 status code indicates that the origin server requires the
             * request to be conditional.
             *
             * Its typical use is to avoid the "lost update" problem, where a client
             * GETs a resource's state, modifies it, and PUTs it back to the server,
             * when meanwhile a third party has modified the state on the server,
             * leading to a conflict.  By requiring requests to be conditional, the
             * server can assure that clients are working with the correct copies.
             *
             * Responses using this status code SHOULD explain how to resubmit the
             * request successfully.  For example:
             *
             * HTTP/1.1 428 Precondition Required
             * Content-Type: text/html
             *
             * <html>
             *   <head>
             *     <title>Precondition Required</title>
             *   </head>
             *   <body>
             *     <h1>Precondition Required</h1>
             *     <p>This request is required to be conditional;
             *     try using "If-Match".</p>
             *   </body>
             * </html>
             *
             * Responses with the 428 status code MUST NOT be stored by a cache.
             */
            val PreconditionRequired = HttpStatusCode(428)

            /**
             * **See Also** [RFC6585#4](https://datatracker.ietf.org/doc/html/rfc6585#section-4)
             *
             * The 429 status code indicates that the user has sent too many
             * requests in a given amount of time ("rate limiting").
             *
             * The response representations SHOULD include details explaining the
             * condition, and MAY include a Retry-After header indicating how long
             * to wait before making a new request.
             *
             * For example:
             *
             * HTTP/1.1 429 Too Many Requests
             * Content-Type: text/html
             * Retry-After: 3600
             *
             * <html>
             *   <head>
             *     <title>Too Many Requests</title>
             *   </head>
             *   <body>
             *     <h1>Too Many Requests</h1>
             *     <p>I only allow 50 requests per hour to this Web site per
             *     logged in user.  Try again soon.</p>
             *   </body>
             * </html>
             *
             * Note that this specification does not define how the origin server
             * identifies the user, nor how it counts requests.  For example, an
             * origin server that is limiting request rates can do so based upon
             * counts of requests on a per-resource basis, across the entire server,
             * or even among a set of servers.  Likewise, it might identify the user
             * by its authentication credentials, or a stateful cookie.
             *
             * Responses with the 429 status code MUST NOT be stored by a cache.
             */
            val TooManyRequests = HttpStatusCode(429)

            /**
             * **See Also** [RFC6585#5](https://datatracker.ietf.org/doc/html/rfc6585#section-5)
             *
             * The 431 status code indicates that the server is unwilling to process
             * the request because its header fields are too large.  The request MAY
             * be resubmitted after reducing the size of the request header fields.
             *
             * It can be used both when the set of request header fields in total is
             * too large, and when a single header field is at fault.  In the latter
             * case, the response representation SHOULD specify which header field
             * was too large.
             *
             * For example:
             *
             * HTTP/1.1 431 Request Header Fields Too Large
             * Content-Type: text/html
             *
             * <html>
             * <head>
             * <title>Request Header Fields Too Large</title>
             * </head>
             * <body>
             * <h1>Request Header Fields Too Large</h1>
             * <p>The "Example" header was too large.</p>
             * </body>
             * </html>
             *
             * Responses with the 431 status code MUST NOT be stored by a cache.
             */
            val RequestHeaderFieldsTooLarge = HttpStatusCode(431)

            /**
             * **See Also** [RFC7725#3](https://datatracker.ietf.org/doc/html/rfc7725#section-3)
             *
             * This status code indicates that the server is denying access to the
             * resource as a consequence of a legal demand.
             *
             * The server in question might not be an origin server.  This type of
             * legal demand typically most directly affects the operations of ISPs
             * and search engines.
             *
             * Responses using this status code SHOULD include an explanation, in
             * the response body, of the details of the legal demand: the party
             * making it, the applicable legislation or regulation, and what classes
             * of person and resource it applies to.  For example:
             * HTTP/1.1 451 Unavailable For Legal Reasons
             * Link: <https://spqr.example.org/legislatione>; rel="blocked-by"
             * Content-Type: text/html
             *
             * <html>
             * <head><title>Unavailable For Legal Reasons</title></head>
             * <body>
             * <h1>Unavailable For Legal Reasons</h1>
             * <p>This request may not be serviced in the Roman Province
             * of Judea due to the Lex Julia Majestatis, which disallows
             * access to resources hosted on servers deemed to be
             * operated by the People's Front of Judea.</p>
             * </body>
             * </html>
             *
             * The use of the 451 status code implies neither the existence nor
             * nonexistence of the resource named in the request.  That is to say,
             * it is possible that if the legal demands were removed, a request for
             * the resource still might not succeed.
             *
             * Note that in many cases clients can still access the denied resource
             * by using technical countermeasures such as a VPN or the Tor network.
             *
             * A 451 response is cacheable by default, i.e., unless otherwise
             * indicated by the method definition or explicit cache controls; see
             * [RFC7234](https://datatracker.ietf.org/doc/html/rfc7234).
             */
            val UnavailableForLegalReasons = HttpStatusCode(451)
        }

        /**
         * **See Also** [RFC2616#10.5](https://datatracker.ietf.org/doc/html/rfc2616#section-10.5)
         *
         * Response status codes beginning with the digit "5" indicate cases in
         * which the server is aware that it has erred or is incapable of
         * performing the request. Except when responding to a HEAD request, the
         * server SHOULD include an entity containing an explanation of the
         * error situation, and whether it is a temporary or permanent
         * condition. User agents SHOULD display any included entity to the
         * user. These response codes are applicable to any request method.
         */
        object ServerError {
            /**
             * **See Also** [RFC2616#10.5.1](https://datatracker.ietf.org/doc/html/rfc2616#section-10.5.1)
             *
             * The server encountered an unexpected condition which prevented it
             * from fulfilling the request.
             */
            val InternalServerError = HttpStatusCode(500)

            /**
             * **See Also** [RFC2616#10.5.2](https://datatracker.ietf.org/doc/html/rfc2616#section-10.5.2)
             *
             * The server does not support the functionality required to fulfill the
             * request. This is the appropriate response when the server does not
             * recognize the request method and is not capable of supporting it for
             * any resource.
             */
            val NotImplemented = HttpStatusCode(501)

            /**
             * **See Also** [RFC2616#10.5.3](https://datatracker.ietf.org/doc/html/rfc2616#section-10.5.3)
             *
             * The server, while acting as a gateway or proxy, received an invalid
             * response from the upstream server it accessed in attempting to
             * fulfill the request.
             */
            val BadGateway = HttpStatusCode(502)

            /**
             * **See Also** [RFC2616#10.5.4](https://datatracker.ietf.org/doc/html/rfc2616#section-10.5.4)
             *
             * The server is currently unable to handle the request due to a
             * temporary overloading or maintenance of the server. The implication
             * is that this is a temporary condition which will be alleviated after
             * some delay. If known, the length of the delay MAY be indicated in a
             * Retry-After header. If no Retry-After is given, the client SHOULD
             * handle the response as it would for a 500 response.
             *
             * Note: The existence of the 503 status code does not imply that a
             * server must use it when becoming overloaded. Some servers may wish
             * to simply refuse the connection.
             */
            val ServiceUnavailable = HttpStatusCode(503)

            /**
             * **See Also** [RFC2616#10.5.5](https://datatracker.ietf.org/doc/html/rfc2616#section-10.5.5)
             *
             * The server, while acting as a gateway or proxy, did not receive a
             * timely response from the upstream server specified by the URI (e.g.
             * HTTP, FTP, LDAP) or some other auxiliary server (e.g. DNS) it needed
             * to access in attempting to complete the request.
             *
             * Note: Note to implementors: some deployed proxies are known to
             * return 400 or 500 when DNS lookups time out.
             */
            val GatewayTimeout = HttpStatusCode(504)

            /**
             * **See Also** [RFC2616#10.5.6](https://datatracker.ietf.org/doc/html/rfc2616#section-10.5.6)
             *
             * The server does not support, or refuses to support, the HTTP protocol
             * version that was used in the request message. The server is
             * indicating that it is unable or unwilling to complete the request
             * using the same major version as the client, as described in [section 3.1](https://datatracker.ietf.org/doc/html/rfc2616#section-3.1),
             * other than with this error message. The response SHOULD contain
             * an entity describing why that version is not supported and what other
             * protocols are supported by that server.
             */
            val HTTPVersionNotSupported = HttpStatusCode(505)

            /**
             * **See Also** [RFC2295#8.1](https://datatracker.ietf.org/doc/html/rfc2295#section-8.1)
             *
             * The 506 status code indicates that the server has an internal
             * configuration error: the chosen variant resource is configured to
             * engage in transparent content negotiation itself, and is therefore
             * not a proper end point in the negotiation process.
             */
            val VariantAlsoNegotiates = HttpStatusCode(506)

            /**
             * **See Also** [RFC4918#11.5](https://datatracker.ietf.org/doc/html/rfc4918#section-11.5)
             *
             * The 507 (Insufficient Storage) status code means the method could not
             * be performed on the resource because the server is unable to store
             * the representation needed to successfully complete the request.  This
             * condition is considered to be temporary.  If the request that
             * received this status code was the result of a user action, the
             * request MUST NOT be repeated until it is requested by a separate user
             * action.
             */
            val InsufficientStorage = HttpStatusCode(507)

            /**
             * **See Also** [RFC5842#7.2](https://datatracker.ietf.org/doc/html/rfc5842#section-7.2)
             *
             * The 508 (Loop Detected) status code indicates that the server
             * terminated an operation because it encountered an infinite loop while
             * processing a request with "Depth: infinity".  This status indicates
             * that the entire operation failed.
             */
            val LoopDetected = HttpStatusCode(508)

            /**
             * **See Also** [RFC2274#7](https://datatracker.ietf.org/doc/html/rfc2774#section-7)
             *
             * The policy for accessing the resource has not been met in the
             * request.  The server should send back all the information necessary
             * for the client to issue an extended request. It is outside the scope
             * of this specification to specify how the extensions inform the
             * client.
             *
             * If the 510 response contains information about extensions that were
             * not present in the initial request then the client MAY repeat the
             * request if it has reason to believe it can fulfill the extension
             * policy by modifying the request according to the information provided
             * in the 510 response. Otherwise the client MAY present any entity
             * included in the 510 response to the user, since that entity may
             * include relevant diagnostic information.
             */
            val NotExtended = HttpStatusCode(510)

            /**
             * **See Also** [RFC6585#6](https://datatracker.ietf.org/doc/html/rfc6585#section-6)
             *
             * The 511 status code indicates that the client needs to authenticate
             * to gain network access.
             *
             * The response representation SHOULD contain a link to a resource that
             * allows the user to submit credentials (e.g., with an HTML form).
             *
             * Note that the 511 response SHOULD NOT contain a challenge or the
             * login interface itself, because browsers would show the login
             * interface as being associated with the originally requested URL,
             * which may cause confusion.
             *
             * The 511 status SHOULD NOT be generated by origin servers; it is
             * intended for use by intercepting proxies that are interposed as a
             * means of controlling access to the network.
             *
             * Responses with the 511 status code MUST NOT be stored by a cache.
             */
            val NetworkAuthenticationRequired = HttpStatusCode(511)
        }
    }
}