 #include "Join.hpp"

#include <vector>
#include <iostream>

using namespace std;

/*
 * Input: Disk, Memory, Disk page ids for left relation, Disk page ids for right relation
 * Output: Vector of Buckets of size (MEM_SIZE_IN_PAGE - 1) after partition
 */

int silence_mode = 1;

vector<Bucket> partition(Disk* disk, Mem* mem, pair<uint, uint> left_rel,
                         pair<uint, uint> right_rel) {
	// TODO: implement partition phase
	vector<Bucket> partitions(MEM_SIZE_IN_PAGE - 1, Bucket(disk));
    uint mem_pos = 0, hash = 0;
    Page* input_page;

    // memory reset
    for (uint i = 0; i < MEM_SIZE_IN_PAGE; i++) {
        mem->mem_page(i)->reset();
    }

	// read left relation from disk to memory
	for (uint i = left_rel.first; i < left_rel.second; i++) {
        // load each page
		mem->loadFromDisk(disk, i, MEM_SIZE_IN_PAGE - 1);
		input_page = mem->mem_page(MEM_SIZE_IN_PAGE - 1);
		for (uint j = 0; j < input_page->size(); j++) {
            Record r = input_page->get_record(j);
            hash = r.partition_hash();
            // find the position to store the record
            mem_pos = hash % (MEM_SIZE_IN_PAGE - 1);
            // if the page is full, flush it to disk
            if(mem->mem_page(mem_pos)->full()){
                partitions[mem_pos].add_left_rel_page(mem->flushToDisk(disk, mem_pos));
            }
            mem->mem_page(mem_pos)->loadRecord(r);
        }
	}

    // flush memory to disk
    for (uint i = 0; i < MEM_SIZE_IN_PAGE - 1; i++) {
        if (!mem->mem_page(i)->empty()) {
            partitions[i].add_left_rel_page(mem->flushToDisk(disk, i));
        }
    }

    if (!silence_mode) {

        for(uint i = 0; i < partitions.size(); i++){
            uint left_size = 0, right_size = 0;

            for (uint j = 0; j < partitions[i].get_left_rel().size(); j++) {
                mem->loadFromDisk(disk, partitions[i].get_left_rel()[j], MEM_SIZE_IN_PAGE - 1);
                left_size += mem->mem_page(MEM_SIZE_IN_PAGE - 1)->size();
            }
            for (uint j = 0; j < partitions[i].get_right_rel().size(); j++) {
                mem->loadFromDisk(disk, partitions[i].get_right_rel()[j], MEM_SIZE_IN_PAGE - 1);
                right_size += mem->mem_page(MEM_SIZE_IN_PAGE - 1)->size();
            }
            cout << "Partition " << i << " left size: " << left_size << endl;
            cout << "Partition " << i << " right size: " << right_size << endl;
        }

        cout << "Left Partitioning done" << endl;
    }

	// read right relation from disk to memory
    for (uint i = right_rel.first; i < right_rel.second; i++) {
        // load each page
        mem->loadFromDisk(disk, i, MEM_SIZE_IN_PAGE - 1);
        input_page = mem->mem_page(MEM_SIZE_IN_PAGE - 1);
        for (uint j = 0; j < input_page->size(); j++) {
            Record r = input_page->get_record(j);
            hash = r.partition_hash();
            // find the position to store the record
            mem_pos = hash % (MEM_SIZE_IN_PAGE - 1);
            // if the page is full, flush it to disk
            if(mem->mem_page(mem_pos)->full()){
                partitions[mem_pos].add_right_rel_page(mem->flushToDisk(disk, mem_pos));
            }
            mem->mem_page(mem_pos)->loadRecord(r);
        }
    }

    // flush memory to disk
    for (uint i = 0; i < MEM_SIZE_IN_PAGE - 1; i++) {
        if (!mem->mem_page(i)->empty()) {
            partitions[i].add_right_rel_page(mem->flushToDisk(disk, i));
        }
    }

    if (!silence_mode) {

        for(uint i = 0; i < partitions.size(); i++){

            uint left_size = 0, right_size = 0;

            for (uint j = 0; j < partitions[i].get_left_rel().size(); j++) {
                mem->loadFromDisk(disk, partitions[i].get_left_rel()[j], MEM_SIZE_IN_PAGE - 1);
                left_size += mem->mem_page(MEM_SIZE_IN_PAGE - 1)->size();
            }
            for (uint j = 0; j < partitions[i].get_right_rel().size(); j++) {
                mem->loadFromDisk(disk, partitions[i].get_right_rel()[j], MEM_SIZE_IN_PAGE - 1);
                right_size += mem->mem_page(MEM_SIZE_IN_PAGE - 1)->size();
            }
            cout << "Partition " << i << " left size: " << left_size << endl;
            cout << "Partition " << i << " right size: " << right_size << endl;
        }

        cout << "Partitioning done" << endl;
    }

	return partitions;
}

/*
 * Input: Disk, Memory, Vector of Buckets after partition
 * Output: Vector of disk page ids for join result
 */
vector<uint> probe(Disk* disk, Mem* mem, vector<Bucket>& partitions) {
	// TODO: implement probe phase

	vector<uint> disk_pages;
	Page* result_page = mem->mem_page(MEM_SIZE_IN_PAGE - 2);
    Page* input_page;
    
    uint mem_pos = 0, hash = 0;
    uint left_size = 0, right_size = 0;
    vector<uint> smaller, larger;

    // iterate through each partition
    for (uint i = 0; i < partitions.size(); i++){

        //memory reset
        for (uint r = 0; r < MEM_SIZE_IN_PAGE - 2; r++) {
            mem->mem_page(r)->reset();
        }

        if (!silence_mode) {
            cout << "Probing Partition " << i << endl;
        }

        // determine which relation is smaller
        left_size = partitions[i].get_left_rel().size();
        right_size = partitions[i].get_right_rel().size();

        if (!silence_mode) {
            cout << "Left page num: " << left_size << endl;
            cout << "Right page num: " << right_size << endl;
        }

        if (left_size < right_size){
            smaller = partitions[i].get_left_rel();
            larger = partitions[i].get_right_rel();
        }else{
            smaller = partitions[i].get_right_rel();
            larger = partitions[i].get_left_rel();
        }
        // read the smaller relation into memory
        for (uint j = 0; j < smaller.size(); j++) {
            mem->loadFromDisk(disk, smaller[j], MEM_SIZE_IN_PAGE - 1);
            input_page = mem->mem_page(MEM_SIZE_IN_PAGE - 1);
            for (uint k = 0; k < input_page->size(); k++) {
                Record r = input_page->get_record(k);
                hash = r.probe_hash();
                // find the position to store the record
                mem_pos = hash % (MEM_SIZE_IN_PAGE - 2);
                
                mem->mem_page(mem_pos)->loadRecord(r);
            }
        }
        if (!silence_mode) {
            cout << "read smaller relation into memory done" << endl;
        }

        //iterate through the larger relation and do comparison and JOIN
        for (uint l = 0; l < larger.size(); l++) {
            mem->loadFromDisk(disk, larger[l], MEM_SIZE_IN_PAGE - 1);
            input_page = mem->mem_page(MEM_SIZE_IN_PAGE - 1);
            for (uint m = 0; m < input_page->size(); m++) {
                Record r = input_page->get_record(m);
                hash = r.probe_hash();
                // join the record
                for(uint n = 0; n < mem->mem_page(hash % (MEM_SIZE_IN_PAGE - 2))->size(); n++){
                    // find exact match
                    if(mem->mem_page(hash % (MEM_SIZE_IN_PAGE - 2))->get_record(n) == r){
                        result_page->loadPair(mem->mem_page(hash % (MEM_SIZE_IN_PAGE - 2))->get_record(n), r);
                        // if the page is full, flush it to disk
                        if(result_page->full()){
                            disk_pages.push_back(mem->flushToDisk(disk, MEM_SIZE_IN_PAGE - 2));
                        }
                    }
                }
            }
        }
    }

    // flush the result page to disk
    if(!result_page->empty()){
        disk_pages.push_back(mem->flushToDisk(disk, MEM_SIZE_IN_PAGE - 2));
    }

    if (!silence_mode) {
        cout << "Probe done" << endl;
    }
      
	return disk_pages;
}
