"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.RandomIdGenerator = void 0;
const cryptoLib = window.crypto || window.msCrypto;
const SPAN_ID_BYTES = 8;
const TRACE_ID_BYTES = 16;
const randomBytesArray = new Uint8Array(TRACE_ID_BYTES);
class RandomIdGenerator {
    /**
     * Returns a random 16-byte trace ID formatted/encoded as a 32 lowercase hex
     * characters corresponding to 128 bits.
     */
    generateTraceId() {
        cryptoLib.getRandomValues(randomBytesArray);
        return this.toHex(randomBytesArray.slice(0, TRACE_ID_BYTES));
    }
    /**
     * Returns a random 8-byte span ID formatted/encoded as a 16 lowercase hex
     * characters corresponding to 64 bits.
     */
    generateSpanId() {
        cryptoLib.getRandomValues(randomBytesArray);
        return this.toHex(randomBytesArray.slice(0, SPAN_ID_BYTES));
    }
    /**
     * Get the hex string representation of a byte array
     *
     * @param byteArray
     */
    toHex(byteArray) {
        const chars = new Array(byteArray.length * 2);
        const alpha = 'a'.charCodeAt(0) - 10;
        const digit = '0'.charCodeAt(0);
        let p = 0;
        for (let i = 0; i < byteArray.length; i++) {
            let nibble = (byteArray[i] >>> 4) & 0xf;
            chars[p++] = nibble > 9 ? nibble + alpha : nibble + digit;
            nibble = byteArray[i] & 0xf;
            chars[p++] = nibble > 9 ? nibble + alpha : nibble + digit;
        }
        return String.fromCharCode.apply(null, chars);
    }
}
exports.RandomIdGenerator = RandomIdGenerator;
//# sourceMappingURL=RandomIdGenerator.js.map