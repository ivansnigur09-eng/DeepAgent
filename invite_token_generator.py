#!/usr/bin/env python3

"""
Invite token generator (local CLI).
Dependencies: pip install pyjwt cryptography

Usage:
1) Generate RSA keypair (one-time):
   python invite_token_generator.py gen-keys --private private.pem --public public.pem
2) Generate token:
   python invite_token_generator.py gen-token --private private.pem --uid user123 --expiry-days 14 --features "dropship,full"

Token is RS256-signed JWT. Keep private.pem offline.
"""
import argparse
import datetime
import jwt
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import rsa

def gen_keys(private_path, public_path, bits=2048):
    key = rsa.generate_private_key(public_exponent=65537, key_size=bits)
    priv = key.private_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PrivateFormat.TraditionalOpenSSL,
        encryption_algorithm=serialization.NoEncryption())
    pub = key.public_key().public_bytes(
        encoding=serialization.Encoding.PEM,
        format=serialization.PublicFormat.SubjectPublicKeyInfo)
    with open(private_path, "wb") as f:
        f.write(priv)
    with open(public_path, "wb") as f:
        f.write(pub)
    print(f"Keys written: {private_path}, {public_path}")

def gen_token(private_path, uid, expiry_days, features):
    with open(private_path, "rb") as f:
        priv = f.read()
    now = datetime.datetime.utcnow()
    payload = {
        "sub": uid,
        "iat": int(now.timestamp()),
        "exp": int((now + datetime.timedelta(days=expiry_days)).timestamp()),
        "features": features.split(",") if features else []
    }
    token = jwt.encode(payload, priv, algorithm="RS256")
    print("TOKEN:", token)

def main():
    p = argparse.ArgumentParser()
    sub = p.add_subparsers(dest="cmd")
    gk = sub.add_parser("gen-keys")
    gk.add_argument("--private", required=True)
    gk.add_argument("--public", required=True)
    gt = sub.add_parser("gen-token")
    gt.add_argument("--private", required=True)
    gt.add_argument("--uid", required=True)
    gt.add_argument("--expiry-days", type=int, default=14)
    gt.add_argument("--features", default="")
    args = p.parse_args()
    if args.cmd == "gen-keys":
        gen_keys(args.private, args.public)
    elif args.cmd == "gen-token":
        gen_token(args.private, args.uid, args.expiry_days, args.features)
    else:
        p.print_help()

if __name__ == "__main__":
    main()