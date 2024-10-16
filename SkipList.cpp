#include <iostream>
#include <vector>
#include <cstdlib>
#include <ctime>

struct SkipListNode {
    int key;
    std::vector<SkipListNode*> forward;

    SkipListNode(int key, int level) : key(key), forward(level, nullptr) {}
};

class SkipList {
public:
    SkipList(int maxLevel, float probability);
    void insert(int key);
    void deleteNode(int key);
    bool search(int key);
    void display();

private:
    int randomLevel();
    int maxLevel;
    float probability;
    int level;
    SkipListNode* header;
    SkipListNode* createNode(int key, int level);
};

SkipList::SkipList(int maxLevel, float probability) {
    this->maxLevel = maxLevel;
    this->probability = probability;
    this->level = 0; // Initialize level to 0
    this->header = new SkipListNode(-1, maxLevel); // Initialize header node
    std::srand(std::time(0)); // Use srand for random seed
}

int SkipList::randomLevel() {
    int lvl = 1;
    while (((double)std::rand() / RAND_MAX) < probability && lvl < maxLevel) {
        lvl++;
    }
    return lvl;
}

SkipListNode* SkipList::createNode(int key, int level) {
    return new SkipListNode(key, level);
}

void SkipList::insert(int key) {
    std::vector<SkipListNode*> update(maxLevel, nullptr);
    SkipListNode* current = header;

    for (int i = level; i >= 0; i--) {
        while (current->forward[i] != nullptr && current->forward[i]->key < key) {
            current = current->forward[i];
        }
        update[i] = current;
    }

    current = current->forward[0]; // Move to the next level

    if (current == nullptr || current->key != key) { // Only insert if key is not present
        int rlevel = randomLevel();

        if (rlevel > level) {
            for (int i = level + 1; i < rlevel; i++) {
                update[i] = header;
            }
            level = rlevel;
        }

        SkipListNode* newNode = createNode(key, rlevel);
        for (int i = 0; i < rlevel; i++) {
            newNode->forward[i] = update[i]->forward[i];
            update[i]->forward[i] = newNode;
        }
    }
}

void SkipList::deleteNode(int key) {
    std::vector<SkipListNode*> update(maxLevel, nullptr);
    SkipListNode* current = header;

    for (int i = level; i >= 0; i--) {
        while (current->forward[i] != nullptr && current->forward[i]->key < key) {
            current = current->forward[i];
        }
        update[i] = current;
    }

    current = current->forward[0];
    if (current != nullptr && current->key == key) {
        for (int i = 0; i <= level; i++) {
            if (update[i]->forward[i] != current) {
                break;
            }
            update[i]->forward[i] = current->forward[i];
        }
        while (level > 0 && header->forward[level] == nullptr) {
            level--;
        }
        delete current; // Free memory for deleted node
    }
}

bool SkipList::search(int key) {
    SkipListNode* current = header;
    for (int i = level; i >= 0; i--) {
        while (current->forward[i] != nullptr && current->forward[i]->key < key) {
            current = current->forward[i];
        }
    }
    current = current->forward[0];
    return current != nullptr && current->key == key;
}

void SkipList::display() {
    for (int i = 0; i <= level; i++) {
        SkipListNode* node = header->forward[i];
        std::cout << "Level " << i << ": ";
        while (node != nullptr) {
            std::cout << node->key << " ";
            node = node->forward[i];
        }
        std::cout << std::endl;
    }
}

int main() {
    SkipList list(4, 0.5);

    list.insert(3);
    list.insert(7);
    list.insert(19);
    list.insert(17);
    list.insert(26);
    list.insert(21);
    std::cout << "Skip List: " << std::endl;
    list.display();

    std::cout << "\nSearch for element 19: " << (list.search(19) ? "exists" : "does not exist") << std::endl;
    std::cout << "\nSkip List after removal of element 19: " << std::endl;
    list.deleteNode(19);
    list.display();
    
    return 0;
}
