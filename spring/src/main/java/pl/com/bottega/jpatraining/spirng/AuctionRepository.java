package pl.com.bottega.jpatraining.spirng;

import org.springframework.data.jpa.repository.JpaRepository;

interface AuctionRepository extends JpaRepository<Auction, String> {
}
