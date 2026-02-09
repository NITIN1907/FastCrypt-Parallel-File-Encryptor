import os
from datetime import datetime

BASE_TEXT = (
    "Encryption is the process of converting information into a secure format "
    "that prevents unauthorized access. Decryption is the reverse operation, "
    "restoring the original data so it can be understood. "
    "Parallel file processing improves performance by utilizing multiple workers. "
    "This project demonstrates how concurrent systems handle file encryption safely. "
)

def makeFiles(path, file_count=1000, target_size=1000):
    os.makedirs(path, exist_ok=True)

    for i in range(file_count):
        filename = os.path.join(path, f"test{i+1}.txt")

        header = (
            f"File Number: {i+1}\n"
            f"Created At: {datetime.now()}\n"
            f"Purpose: Testing parallel file encryption and decryption.\n\n"
        )

        content = header
        while len(content) < target_size:
            content += BASE_TEXT

        content = content[:target_size]

        with open(filename, "w", encoding="utf-8") as file:
            file.write(content)

if __name__ == "__main__":
    makeFiles("test")
