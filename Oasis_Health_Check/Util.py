from cryptography.fernet import Fernet

def generate_key_fernet():
    key = Fernet.generate_key()
    print(key)

#Delphi fernet key
FERNET_KEY = b'lvS0QcqKnxfhfryXEIKRkUMsMA9Nor4kyRIiS95ob1w='
#Delphi fernet key

def encrypt_pass(args):
    cipher_suite = Fernet(FERNET_KEY)
    ciphered_text = cipher_suite.encrypt(bytes(args.encrypt_pass, encoding="ascii"))   #required to be bytes
    print(ciphered_text)

def decrypt_pass(enc_pass, display):
    cipher_suite = Fernet(FERNET_KEY)
    #ciphered_text = bytes(args.decrypt_pass, encoding="ascii")
    if str(enc_pass).startswith("b") == True:
        enc_pass = str(enc_pass).strip("b")

    ciphered_text = bytes(enc_pass, encoding="ascii")
    unciphered_text = (cipher_suite.decrypt(ciphered_text))
    if display == True:
        print(unciphered_text)

    return unciphered_text

'''import crypto.Random
from crypto.Cipher import AES
import hashlib

# salt size in bytes
SALT_SIZE = 16

# number of iterations in the key generation
NUMBER_OF_ITERATIONS = 20

# the size multiple required for AES
AES_MULTIPLE = 16

def generate_key(password, salt, iterations):
    assert iterations > 0

    key = password + salt

    for i in range(iterations):
        key = hashlib.sha256(key).digest()  

    return key

def pad_text(text, multiple):
    extra_bytes = len(text) % multiple

    padding_size = multiple - extra_bytes

    padding = chr(padding_size) * padding_size

    padded_text = text + padding

    return padded_text

def unpad_text(padded_text):
    padding_size = ord(padded_text[-1])

    text = padded_text[:-padding_size]

    return text

def encrypt(plaintext, password):
    salt = Crypto.Random.get_random_bytes(SALT_SIZE)

    key = generate_key(password, salt, NUMBER_OF_ITERATIONS)

    cipher = AES.new(key, AES.MODE_ECB)

    padded_plaintext = pad_text(plaintext, AES_MULTIPLE)

    ciphertext = cipher.encrypt(padded_plaintext)

    ciphertext_with_salt = salt + ciphertext

    return ciphertext_with_salt

def decrypt(ciphertext, password):
    salt = ciphertext[0:SALT_SIZE]

    ciphertext_sans_salt = ciphertext[SALT_SIZE:]

    key = generate_key(password, salt, NUMBER_OF_ITERATIONS)

    cipher = AES.new(key, AES.MODE_ECB)

    padded_plaintext = cipher.decrypt(ciphertext_sans_salt)

    plaintext = unpad_text(padded_plaintext)

    return plaintext
'''

def main():
    print ('In Util Main');
    
if __name__ == '__main__':
    main()
