import os from 'os';

export const normalizePath = (path: string) => {
  return path.startsWith('~') ? path.replace('~', os.homedir()) : path;
};
