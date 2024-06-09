! function (t, e) {
    if ("object" == typeof exports && "object" == typeof module) module.exports = e();
    else if ("function" == typeof define && define.amd) define([], e);
    else {
        var n = e();
        for (var r in n)("object" == typeof exports ? exports : t)[r] = n[r]
    }
}("undefined" != typeof self ? self : this, (function () {
    return function (t) {
        var e = {};

        function n(r) {
            if (e[r]) return e[r].exports;
            var o = e[r] = {
                i: r,
                l: !1,
                exports: {}
            };
            return t[r].call(o.exports, o, o.exports, n), o.l = !0, o.exports
        }
        return n.m = t, n.c = e, n.d = function (t, e, r) {
            n.o(t, e) || Object.defineProperty(t, e, {
                enumerable: !0,
                get: r
            })
        }, n.r = function (t) {
            "undefined" != typeof Symbol && Symbol.toStringTag && Object.defineProperty(t, Symbol.toStringTag, {
                value: "Module"
            }), Object.defineProperty(t, "__esModule", {
                value: !0
            })
        }, n.t = function (t, e) {
            if (1 & e && (t = n(t)), 8 & e) return t;
            if (4 & e && "object" == typeof t && t && t.__esModule) return t;
            var r = Object.create(null);
            if (n.r(r), Object.defineProperty(r, "default", {
                    enumerable: !0,
                    value: t
                }), 2 & e && "string" != typeof t)
                for (var o in t) n.d(r, o, function (e) {
                    return t[e]
                }.bind(null, o));
            return r
        }, n.n = function (t) {
            var e = t && t.__esModule ? function () {
                return t.default
            } : function () {
                return t
            };
            return n.d(e, "a", e), e
        }, n.o = function (t, e) {
            return Object.prototype.hasOwnProperty.call(t, e)
        }, n.p = "/", n(n.s = 1)
    }([function (t, e) {
        t.exports = o, t.exports.parse = o, t.exports.stringify = function t(e) {
            "Feature" === e.type && (e = e.geometry);

            function n(t) {
                return t.join(" ")
            }

            function r(t) {
                return t.map(n).join(", ")
            }

            function o(t) {
                return t.map(r).map(i).join(", ")
            }

            function i(t) {
                return "(" + t + ")"
            }
            switch (e.type) {
                case "Point":
                    return "POINT (" + n(e.coordinates) + ")";
                case "LineString":
                    return "LINESTRING (" + r(e.coordinates) + ")";
                case "Polygon":
                    return "POLYGON (" + o(e.coordinates) + ")";
                case "MultiPoint":
                    return "MULTIPOINT (" + r(e.coordinates) + ")";
                case "MultiPolygon":
                    return "MULTIPOLYGON (" + (e.coordinates.map(o).map(i).join(", ") + ")");
                case "MultiLineString":
                    return "MULTILINESTRING (" + o(e.coordinates) + ")";
                case "GeometryCollection":
                    return "GEOMETRYCOLLECTION (" + e.geometries.map(t).join(", ") + ")";
                default:
                    throw new Error("stringify requires a valid GeoJSON Feature or geometry object as input")
            }
        };
        var n = /[-+]?([0-9]*\.[0-9]+|[0-9]+)([eE][-+]?[0-9]+)?/,
            r = new RegExp("^" + n.source + "(\\s" + n.source + "){1,}");

        function o(t) {
            var e, n = t.split(";"),
                o = n.pop(),
                i = (n.shift() || "").split("=").pop(),
                s = 0;

            function a(t) {
                var e = o.substring(s).match(t);
                return e ? (s += e[0].length, e[0]) : null
            }

            function u() {
                a(/^\s*/)
            }

            function l() {
                u();
                for (var t, e = 0, n = [], o = [n], i = n; t = a(/^(\()/) || a(/^(\))/) || a(/^(,)/) || a(r);) {
                    if ("(" === t) o.push(i), i = [], o[o.length - 1].push(i), e++;
                    else if (")" === t) {
                        if (0 === i.length) return null;
                        if (!(i = o.pop())) return null;
                        if (0 === --e) break
                    } else if ("," === t) i = [], o[o.length - 1].push(i);
                    else {
                        if (t.split(/\s/g).some(isNaN)) return null;
                        Array.prototype.push.apply(i, t.split(/\s/g).map(parseFloat))
                    }
                    u()
                }
                return 0 !== e ? null : n
            }

            function p() {
                for (var t, e, n = []; e = a(r) || a(/^(,)/);) "," === e ? (n.push(t), t = []) : e.split(/\s/g).some(isNaN) || (t || (t = []), Array.prototype.push.apply(t, e.split(/\s/g).map(parseFloat))), u();
                return t ? (n.push(t), n.length ? n : null) : null
            }

            function c() {
                return function () {
                    if (!a(/^(point(\sz)?)/i)) return null;
                    if (u(), !a(/^(\()/)) return null;
                    var t = p();
                    return t ? (u(), a(/^(\))/) ? {
                        type: "Point",
                        coordinates: t[0]
                    } : null) : null
                }() || function () {
                    if (!a(/^(linestring(\sz)?)/i)) return null;
                    if (u(), !a(/^(\()/)) return null;
                    var t = p();
                    return t && a(/^(\))/) ? {
                        type: "LineString",
                        coordinates: t
                    } : null
                }() || function () {
                    if (!a(/^(polygon(\sz)?)/i)) return null;
                    u();
                    var t = l();
                    return t ? {
                        type: "Polygon",
                        coordinates: t
                    } : null
                }() || function () {
                    if (!a(/^(multipoint)/i)) return null;
                    u();
                    var t = o.substring(o.indexOf("(") + 1, o.length - 1).replace(/\(/g, "").replace(/\)/g, "");
                    o = "MULTIPOINT (" + t + ")";
                    var e = l();
                    return e ? (u(), {
                        type: "MultiPoint",
                        coordinates: e
                    }) : null
                }() || function () {
                    if (!a(/^(multilinestring)/i)) return null;
                    u();
                    var t = l();
                    return t ? (u(), {
                        type: "MultiLineString",
                        coordinates: t
                    }) : null
                }() || function () {
                    if (!a(/^(multipolygon)/i)) return null;
                    u();
                    var t = l();
                    return t ? {
                        type: "MultiPolygon",
                        coordinates: t
                    } : null
                }() || function () {
                    var t, e = [];
                    if (!a(/^(geometrycollection)/i)) return null;
                    if (u(), !a(/^(\()/)) return null;
                    for (; t = c();) e.push(t), u(), a(/^(,)/), u();
                    return a(/^(\))/) ? {
                        type: "GeometryCollection",
                        geometries: e
                    } : null
                }()
            }
            return (e = c()) && i.match(/\d+/) && (e.crs = {
                type: "name",
                properties: {
                    name: "urn:ogc:def:crs:EPSG::" + i
                }
            }), e
        }
    }, function (t, e, n) {
        "use strict";
        n.r(e), n.d(e, "Directions", (function () {
            return g
        }));
        /*! *****************************************************************************
        Copyright (c) Microsoft Corporation. All rights reserved.
        Licensed under the Apache License, Version 2.0 (the "License"); you may not use
        this file except in compliance with the License. You may obtain a copy of the
        License at http://www.apache.org/licenses/LICENSE-2.0

        THIS CODE IS PROVIDED ON AN *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
        KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION ANY IMPLIED
        WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A PARTICULAR PURPOSE,
        MERCHANTABLITY OR NON-INFRINGEMENT.

        See the Apache Version 2.0 License for specific language governing permissions
        and limitations under the License.
        ***************************************************************************** */
        var r = function (t, e) {
            return (r = Object.setPrototypeOf || {
                    __proto__: []
                }
                instanceof Array && function (t, e) {
                    t.__proto__ = e
                } || function (t, e) {
                    for (var n in e) e.hasOwnProperty(n) && (t[n] = e[n])
                })(t, e)
        };
        var o = function () {
            return (o = Object.assign || function (t) {
                for (var e, n = 1, r = arguments.length; n < r; n++)
                    for (var o in e = arguments[n]) Object.prototype.hasOwnProperty.call(e, o) && (t[o] = e[o]);
                return t
            }).apply(this, arguments)
        };
        var i = 0,
            s = 1,
            a = {
                fast: "#22aa01",
                normal: "#ffc402",
                slow: "#f72000",
                "slow-jams": "#81020d",
                "no-traffic": "#0f6ec1",
                ignore: "#0f6ec1",
                pedestrian: "#626262",
                "pedestrian-underground": "#626262",
                inactive: "#b4bcd2",
                border: "#ffffff",
                border2: "#ff141414",
                hovered: "#000000"
            },
            u = {
                size: [22, 22],
                offset: [11, 11],
                img: "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjIiIGhlaWdodD0iMjIiIHZpZXdCb3g9IjAgMCAyMiAyMiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cGF0aCBmaWxsPSIjMDAwMDAwIiBmaWxsLW9wYWNpdHk9IjAuMTUiIGQ9Ik0xMSAyMkMxNy4wNzUxIDIyIDIyIDE3LjA3NTEgMjIgMTFDMjIgNC45MjQ4NyAxNy4wNzUxIDAgMTEgMEM0LjkyNDg3IDAgMCA0LjkyNDg3IDAgMTFDMCAxNy4wNzUxIDQuOTI0ODcgMjIgMTEgMjJaIi8+CiAgPHBhdGggZmlsbD0iI2ZmZmZmZiIgZD0iTTExIDIxQzE2LjUyMjggMjEgMjEgMTYuNTIyOCAyMSAxMUMyMSA1LjQ3NzE1IDE2LjUyMjggMSAxMSAxQzUuNDc3MTUgMSAxIDUuNDc3MTUgMSAxMUMxIDE2LjUyMjggNS40NzcxNSAyMSAxMSAyMVoiLz4KICA8cGF0aCBmaWxsPSIjMGY2ZWMxIiBkPSJNMTEgMTlDMTUuNDE4MyAxOSAxOSAxNS40MTgzIDE5IDExQzE5IDYuNTgxNzIgMTUuNDE4MyAzIDExIDNDNi41ODE3MiAzIDMgNi41ODE3MiAzIDExQzMgMTUuNDE4MyA2LjU4MTcyIDE5IDExIDE5WiIvPgo8L3N2Zz4K"
            },
            l = 14,
            p = "#ffffff";

        function c(t) {
            switch (t) {
                case "fast":
                case "normal":
                case "slow":
                case "slow-jams":
                case "no-traffic":
                    return t;
                default:
                    return "ignore"
            }
        }

        function f(t) {
            return t.map((function (e, n) {
                var r = String(n);
                return 0 === n && (r = "A"), n === t.length - 1 && (r = "B"), {
                    position: e,
                    label: {
                        text: r,
                        fontSize: l,
                        color: p
                    },
                    icon: u
                }
            }))
        }
        var d = n(0),
            h = function () {
                function t(t, e) {
                    this.defaultStyle = {
                        routeLineWidth: 10.5,
                        substrateLineWidth: 21.5,
                        haloLineWidth: 23.1
                    }, this.map = t, this.settings = e, this.polylines = [], this.markers = [], this.nextPolylinePhase = i, this.nextPointPhase = s
                }
                return t.prototype.draw = function (t) {
                    this.drawPoints(t.points), this.drawRoutes(t.routes, t.activeRouteId, t.style)
                }, t.prototype.clear = function () {
                    this.draw({
                        routes: [],
                        points: []
                    })
                }, t.prototype.drawPoints = function (t) {
                    for (var e = 0, n = this.markers; e < n.length; e++) {
                        (l = n[e]).destroy()
                    }
                    this.markers = [], this.nextPointPhase = s;
                    for (var r = 0; r < t.length; r++) {
                        var o = t[r],
                            i = o.position,
                            a = o.label,
                            u = o.icon,
                            l = new mapgl.Marker(this.map, {
                                coordinates: i,
                                icon: u.img,
                                size: u.size,
                                anchor: u.offset,
                                zIndex: this.getNextPointPhase(),
                                label: {
                                    text: a.text,
                                    fontSize: a.fontSize,
                                    color: a.color,
                                    anchor: [0, 0],
                                    zIndex: this.getNextPointPhase(),
                                    haloRadius: 0,
                                    haloColor: "#ffffff",
                                    letterSpacing: 0,
                                    lineHeight: 1.2,
                                    minZoom: NaN,
                                    maxZoom: NaN
                                }
                            });
                        this.markers.push(l)
                    }
                }, t.prototype.drawRoutes = function (t, e, n) {
                    for (var r = 0, s = this.polylines; r < s.length; r++) {
                        s[r].polyline.destroy()
                    }
                    this.polylines = [], this.nextPolylinePhase = i;
                    for (var a, u = o(o({}, this.defaultStyle), n), l = 0, p = t; l < p.length; l++) {
                        var c = p[l];
                        c.id === e ? a = c : this.drawRoute(c, !1, u)
                    }
                    a && this.drawRoute(a, !0, u)
                }, t.prototype.drawRoute = function (t, e, n) {
                    for (var r = t.id, o = t.sections, i = this.getNextPolylinePhase(), s = this.getNextPolylinePhase(), a = 0, u = o; a < u.length; a++) {
                        var l = u[a],
                            p = Object(d.parse)(l.geometry);
                        if (p) {
                            var c = p.coordinates,
                                f = this.settings.lineStyles,
                                h = void 0 !== f[l.type] ? l.type : "inactive",
                                y = e ? h : "inactive",
                                g = f[y],
                                m = g.color,
                                v = g.borderColor,
                                M = g.border2Color,
                                I = new mapgl.Polyline(this.map, {
                                    coordinates: c,
                                    zIndex: this.getNextPolylinePhase(),
                                    zIndex2: s,
                                    zIndex3: i,
                                    color: m,
                                    color2: v,
                                    color3: M,
                                    width: n.routeLineWidth,
                                    width2: n.substrateLineWidth,
                                    width3: n.haloLineWidth,
                                    interactive: !0,
                                    maxZoom: NaN,
                                    minZoom: NaN
                                });
                            this.polylines.push({
                                routeId: r,
                                type: y,
                                polyline: I
                            })
                        }
                    }
                }, t.prototype.getNextPolylinePhase = function () {
                    return this.nextPolylinePhase += 1e-6
                }, t.prototype.getNextPointPhase = function () {
                    return this.nextPointPhase += 1e-6
                }, t
            }(),
            y = function (t, e) {
                return {
                    color: e[t],
                    hoveredColor: e.hovered,
                    borderColor: e.border,
                    hoveredBorderColor: e.border,
                    border2Color: e.border2,
                    hoveredBorder2Color: e.border2
                }
            },
            g = function (t) {
                function e(e, n) {
                    var r = t.call(this) || this;
                    r.map = e, r.options = n;
                    var o, i = {
                        lineStyles: {
                            normal: y("normal", o = a),
                            fast: y("fast", o),
                            ignore: y("ignore", o),
                            "no-traffic": y("no-traffic", o),
                            slow: y("slow", o),
                            "slow-jams": y("slow-jams", o),
                            pedestrian: y("pedestrian", o),
                            "pedestrian-underground": y("pedestrian-underground", o),
                            inactive: y("inactive", o)
                        }
                    };
                    return r.ppnaDrawer = new h(r.map, i), r
                }
                return function (t, e) {
                    function n() {
                        this.constructor = t
                    }
                    r(t, e), t.prototype = null === e ? Object.create(e) : (n.prototype = e.prototype, new n)
                }(e, t), e.prototype.clear = function () {
                    this.ppnaDrawer.clear(), this.map.setStyleOptions({
                        traffic: !1
                    })
                }, e.prototype.carRoute = function (t) {
                    var e = t.points,
                        n = t.style;
                    return this.findAndDrawRoute({
                        type: "jam",
                        points: e,
                        style: n
                    })
                }, e.prototype.pedestrianRoute = function (t) {
                    var e = t.points,
                        n = t.style;
                    return this.findAndDrawRoute({
                        type: "pedestrian",
                        points: e,
                        style: n
                    })
                }, e.prototype.findAndDrawRoute = function (t) {
                    var e = this,
                        n = t.points,
                        r = t.type,
                        o = t.style;
                    if (n.length < 2) throw new Error("At least two points are required");
                    var i = {
                        type: r,
                        point_a_name: "Source",
                        point_b_name: "Target",
                        locale: "en",
                        points: n.map((function (t, e) {
                            return {
                                x: t[0],
                                y: t[1],
                                type: 0 === e || e === n.length - 1 ? "pedo" : "pref"
                            }
                        }))
                    };
                    return fetch("https://catalog.api.2gis.ru/carrouting/6.0.0/global?key=" + this.options.directionsApiKey, {
                        method: "post",
                        headers: {
                            'X-App-Id': 'com.example.turmurom',
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(i)
                    }).then((function (t) {
                        if (200 !== t.status) throw new Error("HTTP code is " + t.status);
                        return t.json()
                    })).then((function (n) {
                        e.emit("directionsLoaded", {
                            routes: n.result || []
                        }), e.drawRoute(n.result || [], t.points, o)
                    }))
                }, e.prototype.drawRoute = function (t, e, n) {
                    var r = function (t, e) {
                        var n = [];
                        return e.forEach((function (e) {
                            var r = {
                                id: e.id,
                                sections: []
                            };
                            e.begin_pedestrian_path && r.sections.push({
                                type: "pedestrian",
                                geometry: e.begin_pedestrian_path.geometry.selection
                            }), (e.maneuvers || []).forEach((function (e) {
                                var n = e.outcoming_path && e.outcoming_path.geometry;
                                n && n.forEach((function (e) {
                                    r.sections.push({
                                        type: "pedestrian" !== t ? c(e.color) : "pedestrian",
                                        geometry: e.selection
                                    })
                                }))
                            })), e.end_pedestrian_path && r.sections.push({
                                type: "pedestrian",
                                geometry: e.end_pedestrian_path.geometry.selection
                            }), n.push(r)
                        })), n
                    }("ppna", t)[0];
                    this.map.setStyleOptions({
                        traffic: !0
                    }), this.ppnaDrawer.draw({
                        routes: [r],
                        points: f(e),
                        activeRouteId: r.id,
                        style: n
                    })
                }, e
            }(function () {
                function t() {
                    this.events = {}
                }
                return t.prototype.on = function (t, e) {
                    var n = this.events[t];
                    return n || (n = this.events[t] = []), n.push(e), this
                }, t.prototype.once = function (t, e) {
                    var n = this,
                        r = function (o) {
                            n.off(t, r), e.call(n, o)
                        };
                    return this.on(t, r), this
                }, t.prototype.off = function (t, e) {
                    var n = this.events[t];
                    if (!n) return this;
                    var r = n.indexOf(e);
                    return -1 !== r && n.splice(r, 1), this
                }, t.prototype.emit = function (t, e) {
                    var n = this.events[t];
                    if (!n) return this;
                    for (var r = n.slice(), o = 0; o < r.length; o++) r[o].call(this, e);
                    return this
                }, t
            }());
        "undefined" != typeof window && ("mapgl" in window ? mapgl.Directions = g : (window.__mapglPlugins || (window.__mapglPlugins = {}), window.__mapglPlugins.Directions = g))
    }])
}));
