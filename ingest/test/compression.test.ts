import * as zlib from 'zlib';
import { Gzip } from '../src/utils/compression';

const rgx = /Decompressed data does not match expected size >> Is: \d+ \| Expected: \d+/i;

// Not gonna add sync functions to Gzip, since they're not used anywhere currently
describe('gunzip', () => {
  it('should reject if input buffer is empty or null', async () => {
    await expect(Gzip.gunzip(null, 10, 20)).rejects.toThrow('Input buffer is empty or null');
    await expect(Gzip.gunzip(Buffer.alloc(0), 10, 20)).rejects.toThrow('Input buffer is empty or null');
  });

  it('should reject if buffer is not valid gzip', async () => {
    const buffer = Buffer.from('invalid data');
    await expect(Gzip.gunzip(buffer, 10, 20)).rejects.toThrow('Input buffer is not valid gzip');
  });

  it('should reject if decompressed data exceeds expected size', async () => {
    const buffer = zlib.gzipSync('test');
    await expect(Gzip.gunzip(buffer, 2, 20)).rejects.toThrow(rgx);
  });

  it('should reject if decompressed data exceeds maximum size', async () => {
    const buffer = zlib.gzipSync('test');
    await expect(Gzip.gunzip(buffer, 4, 2)).rejects.toThrow('Decompressed data exceeds maximum size');
  });

  it('should reject if decompression ends without result', async () => {
    const buffer = zlib.gzipSync('no result');
    await expect(Gzip.gunzip(buffer, 20, 20)).rejects.toThrow(rgx);
  });

  it('should resolve with decompressed data if successful', async () => {
    const data = 'test';
    const buffer = await Gzip.compressString(data);
    const result = await Gzip.gunzip(buffer, data.length, 5);
    expect(result).toEqual(data);
  });
});
