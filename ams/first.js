const base64PublicKey = 'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApY79RWhCiOSbOJJIqDXn8TS7QB9yanTHbhENCuavsVxgVIc0IHgcT45uTd38a7wQ00zPSIJOoVSz9rnCfsN3COCewJoxcEDrPpWtky8AW8bGAaQx0PKBOahffpkbJqJDw4+qaj2wYHrWhnm0XNl9bnbGg0skOMUtrl4NQyJyVX5jI7pjvuv92xaVMUYcFsQmWF9Jdj3e5WzqyYHu/ACcuAwyfuH52pwQ0LB0432TvDgvtS8oukWI30Dmhtma2M0VWmV3sDH9vVciU8LPptvySik7401fnOTnPA2wKMA4j/QJaFEZVWNmL1lnFDLlx9P+aOB48ZsMD6kyWN77OlbPRQIDAQAB';

const crypto = require('crypto');

const RSA_MODULUS_LENGTH = 2048;

// Generate a new RSA keypair
const keypair = crypto.generateKeyPairSync('rsa', {
  modulusLength: RSA_MODULUS_LENGTH,
});

// Extract the public and private keys from the keypair
const publicKey = keypair.publicKey;
const privateKey = keypair.privateKey;

// Use the public key to encrypt a message
const message = 'Hello, World!';
const buffer = Buffer.from(message);
const encrypted = crypto.publicEncrypt(publicKey, buffer);

console.log('Encrypted:', encrypted.toString('base64'));

// Use the private key to decrypt the message
const decrypted = crypto.privateDecrypt(privateKey, encrypted);

console.log('Decrypted:', decrypted.toString());
