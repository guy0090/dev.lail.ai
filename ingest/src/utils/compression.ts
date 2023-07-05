import zlib from 'zlib';

/**
 * Gzip (de)compression utility
 */
export const Gzip = {
  HEADER: Buffer.from([0x1f, 0x8b]),

  /**
   * Compress a buffer with gzip compression.
   * @param {Buffer} buffer The buffer to compress
   * @returns {Promise<Buffer>} A promise that resolves to the compressed buffer
   */
  compress(buffer: Buffer): Promise<Buffer> {
    return new Promise((resolve, reject) => {
      zlib.gzip(buffer, (err, result) => {
        if (err) {
          reject(err);
        } else {
          resolve(result);
        }
      });
    });
  },

  /**
   * Compress a string with gzip compression.
   * @param {string} string The string to compress
   * @returns {Promise<Buffer>} The compressed string
   */
  compressString(string: any, encoding: any = 'utf-8'): Promise<Buffer> {
    return new Promise((resolve, reject) => {
      const buffer = Buffer.from(string, encoding);
      zlib.gzip(buffer, (err, result) => {
        if (err) {
          reject(err);
        } else {
          resolve(result);
        }
      });
    });
  },

  /**
   * Decompress a buffer to string format.
   * @param {Buffer} buffer The buffer to decompress.
   * @returns {Promise<String>} A promise that resolves to the decompressed string.
   */
  decompress(buffer: Buffer, encoding: any = 'utf-8'): Promise<string> {
    return new Promise((resolve, reject) => {
      zlib.gunzip(buffer, (err, result) => {
        if (err) {
          reject(err);
        } else {
          resolve(result.toString(encoding));
        }
      });
    });
  },

  /**
   * Async gunzip a buffer.
   *
   * Could also use zlib.gunzipSync() - considering it.
   *
   * @param {Buffer} buffer The buffer to decompress
   * @param {number} expectedSize The expected size of the decompressed buffer
   * @param {number} maxSize The maximum size of the decompressed buffer
   */
  gunzip(buffer: Buffer, expectedSize: number, maxSize: number): Promise<string> {
    return new Promise((resolve, reject) => {
      if (!buffer || buffer.length < 3) {
        reject(new Error('Input buffer is empty or null'));
        return;
      }

      if (buffer.subarray(0, 2).compare(this.HEADER) !== 0) {
        reject(new Error('Input buffer is not valid gzip'));
        return;
      }

      const gunzip = zlib.createGunzip();
      const data = [];

      let size = 0;
      let error: Error | undefined;
      let result: string | undefined;
      gunzip.on('data', chunk => {
        size += chunk.length;

        if (size > maxSize) {
          gunzip.destroy(new Error('Decompressed data exceeds maximum size'));
        }

        data.push(chunk);
      });

      gunzip.on('end', () => {
        result = Buffer.concat(data).toString('utf-8');
        if (result.length !== expectedSize) {
          gunzip.destroy(new Error(`Decompressed data does not match expected size >> Is: ${result.length} | Expected: ${expectedSize}`));
        } else {
          gunzip.close();
        }
      });

      gunzip.on('close', () => {
        if (error) {
          reject(error || new Error('Decompression failed'));
        } else if (result) {
          resolve(result);
        } else {
          reject(new Error('Decompression ended without result or error')); // We never reached the expected size
        }
      });

      gunzip.on('error', err => {
        error = err;
      });

      gunzip.write(buffer);
      gunzip.end();
    });
  },
};
