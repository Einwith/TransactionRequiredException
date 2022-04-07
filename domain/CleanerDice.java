package com.lixar.apba.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "cleaner_dice")
public class CleanerDice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO,
    		generator = "native"
    )
    @GenericGenerator(
    		name = "native",
    		strategy = "native"
    )
    private Integer id;

    @Column(name="gid")
	private Integer gid;

    @Column(name="dice_ids")
	private String dice_ids;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public String getDice_ids() {
		return dice_ids;
	}

	public void setDice_ids(String dice_ids) {
		this.dice_ids = dice_ids;
	}
	
	public int[] getDiceIdsArray() {
		String[] ids = dice_ids.split(",");
		int[] result = new int[ids.length];
		for (int i = 0; i < ids.length; i++) {
			result[i] = Integer.parseInt(ids[i]);
		}
		return result;
	}
    
    
}
