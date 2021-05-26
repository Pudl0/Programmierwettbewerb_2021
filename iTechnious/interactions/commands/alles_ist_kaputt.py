import requests


def run(req, client=None, options=None, mysql=None):
    r = {"nsfw": True}
    while r["nsfw"]:
        r = requests.get("https://meme-api.herokuapp.com/gimme/programmingmemes").json()
        image = r["url"]
        author = r["author"]

    embed = {
        "title": "Lass dich nicht unterkriegen!",
        "description": "Ein bisschen Stackoverflow und Rumgewerkel bekommen das schon wieder hin!",
        "image": {"url": image},
        "author": {"name": author, "url": r["postLink"]}
    }

    return {"type": 4,
            "data": {
                "tts": False,
                "content": "",
                "embeds": [embed],
                "allowed_mentions": []
                }
            }
